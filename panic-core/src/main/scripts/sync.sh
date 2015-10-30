#!/bin/bash


# script used to sync the binary files on the remote server


MOUNT_POINT="mnt/"
TARGET_POINT="target src/main/scripts"


rsync -avuR --progress --stats $TARGET_POINT $MOUNT_POINT

for t in $TARGET_POINT; do chmod  -R 777 $MOUNT_POINT; done
