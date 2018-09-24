#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Time    : 2018/9/24 0024 上午 9:20
# @Author  : Tony GZ
# @File    : downloadM3u8.py

import sys
import requests


def download(url, filepath):
    try:
        response = requests.get(url)
        with open(filepath, 'ab+') as f:
            f.write(response.content)
        print("successful")
    except Exception as e:
        print(e)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        download(sys.argv[1], sys.argv[2])
    else:
        print("argv is wrong")
        print(len(sys.argv))
        print("0: " + sys.argv[0])
        print("1: " + sys.argv[1])
        print("2: " + sys.argv[2])
        print("3: " + sys.argv[3])
        print("4: " + sys.argv[4])
