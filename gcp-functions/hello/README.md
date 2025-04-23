# README

This is a small example of [Cloud Run functions](https://cloud.google.com/functions).

## Features

- Uses Scala CLI for building and packaging
- Deploys using Terraform
- Simple HTTP function that returns "Hello!"
- Logs the request method and url

## Prerequisites

- [Scala CLI](https://scala-cli.virtuslab.org/) installed
- [Terraform](https://developer.hashicorp.com/terraform/) installed
- [gcloud CLI](https://cloud.google.com/sdk/gcloud) installed
- Authenticated with Google Cloud (`gcloud auth application-default login`)

## Running the app

1. Build the function
    ```
    scala-cli package --assembly --preamble=false HelloFunction.scala -o out/cf-hello.jar --force
    ```
1. Deploy the function
    ```
    terraform init
    terraform apply
    ```
1. Call the function
    ```
    curl "$(terraform output -raw function_url)"; echo
    Hello!
    ```
    > Note: The function URL is exposed as a Terraform output named `function_url`.
