#!/bin/bash
ps ax |grep java |grep HBaseExporter| awk '{print $1}' |xargs kill -9
