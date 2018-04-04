#!/bin/bash

set -e

bx wsk action invoke cloud-object-storage/object-write -b -p Bucket csantana -p Key data.txt -p Body "Hello World"
bx wsk action invoke cloud-object-storage/object-read  -b -p Bucket csantana -p Key data.txt
bx wsk action invoke cloud-object-storage/object-write -b -p Bucket csantana -p Key data.txt