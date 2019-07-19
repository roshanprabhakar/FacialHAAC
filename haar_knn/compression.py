import cv2
import numpy as np
import time

img = cv2.imread("testimg.jpg")


def getRGBfromI(RGBint):
    blue = RGBint & 255
    green = (RGBint >> 8) & 255
    red = (RGBint >> 16) & 255
    return [red, green, blue]


def getIfromRGB(rgb):
    red = rgb[0]
    green = rgb[1]
    blue = rgb[2]
    RGBint = (red << 16) + (green << 8) + blue
    return RGBint


def resize(img, nw, nh):

    scaled = []

    vf = len(img) // nh
    hf = len(img[0]) // nw

    for r in range(nh - 1):

        row = []

        for c in range(nw - 1):

            max = 0

            for i in range(nh):
                for j in range(nw):

                    if img[r * vf + i][c * hf + j] > max: max = img[r * vf + i][c * hf + j]

            row.append(sum / (nw * nh))

        scaled.append(row)
        print("row: " + str(r) + " out of: " + str(nh))

    print(len(scaled))
    print(len(scaled[0]))


resize(img, 100, 100)

#
# print(len(img), len(img[0]))
# print(len(newImg), len(img[0]))
#
# cv2.imshow("processed", newImg)
# cv2.waitKey(0)
# time.sleep(100000)

# cv2.imshow("image", resize(img, 100, 100))
# cv2.waitKey(0)
# time.sleep(10000)
