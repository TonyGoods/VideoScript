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
        print("wrong")
