terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.43.1"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

resource "random_id" "bucket_suffix" {
  byte_length = 8
}

resource "google_service_account" "cf" {
  account_id = "${var.prefix}-cf"
}

resource "google_storage_bucket" "gcf_source" {
  name                        = "${var.prefix}-gcf-source-${random_id.bucket_suffix.hex}"
  location                    = var.region
  uniform_bucket_level_access = true
  force_destroy               = true
}

data "archive_file" "function_source" {
  type        = "zip"
  source_dir  = "./out"
  output_path = "./${var.prefix}-source.zip"
}

resource "google_storage_bucket_object" "function_archive" {
  name   = "${var.prefix}-source-${data.archive_file.function_source.output_md5}.zip"
  bucket = google_storage_bucket.gcf_source.name
  source = data.archive_file.function_source.output_path
}

resource "google_cloudfunctions2_function" "hello" {
  name     = "${var.prefix}-hello"
  location = var.region

  build_config {
    runtime     = "java21"
    entry_point = "example.HelloFunction"
    source {
      storage_source {
        bucket = google_storage_bucket.gcf_source.name
        object = google_storage_bucket_object.function_archive.name
      }
    }
  }

  service_config {
    min_instance_count    = 0
    max_instance_count    = 1
    available_memory      = "256M"
    timeout_seconds       = 60
    service_account_email = google_service_account.cf.email
  }
}

resource "google_cloud_run_service_iam_member" "public_function_invoker" {
  location = google_cloudfunctions2_function.hello.location
  service  = google_cloudfunctions2_function.hello.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}

output "function_url" {
  value = google_cloudfunctions2_function.hello.url
}
