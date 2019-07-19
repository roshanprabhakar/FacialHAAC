import math
import os
from os import system
import time

import cv2 as cv
import numpy as np

time_spent_calculating_distances = 0
time_spent_calculating_bgr_distances = 0
total_time = 0

# face, left eye, right eye, nose, mouth: coordinates given as subordinate to face coordinates
FEATURES = []


# def roi_for_forehead():
#     y_values = []
#     x_values = []
#
#     # finding top and bottom y values (from face)
#     y_values.append(FEATURES[0][1])
#     y_values.append(FEATURES[higher_eye_index()][1])
#
#     # finding left and right x values
#     x_values.append(FEATURES[2][0])
#     x_values.append(FEATURES[1][0] + FEATURES[1][3])
#
#     # def(roi) = x (t) ,y (t) ,w (r - l),h (t - b)
#
#     x = x_values[0]
#     y = y_values[0]
#     w = x_values[1] - x_values[0]
#     h = y_values[0] - y_values[1]
#
#     return [x, y, w, h]

def write_features_to_disk():
    features = open("features.txt", "w")
    for roi in FEATURES:
        features.write(str(roi) + "\n")


def higher_eye_index():
    if FEATURES[1][1] > FEATURES[2][1]:
        return 1
    else:
        return 2


def load_images_from_folder(folder):
    images = []
    for filename in os.listdir(folder):
        img = cv.imread(os.path.join(folder, filename))
        if img is not None:
            images.append(img)
    return images


def find_bigger_img(img1, img2):
    if len(img1) * len(img1[0]) > len(img2) * len(img2[0]):
        return img1, img2
    else:
        return img2, img1


def get_resized_image(smaller, bigger):
    return cv.resize(bigger, (len(smaller[0]), len(smaller)), cv.INTER_AREA)


# knn distance between two different images
# to be used to compare different ROI to determine most likely accurate
# (i.e. used to determing which 2 out of three roi are eyes)
# @param image arrays without location
def distance(img1, img2):
    global time_spent_calculating_distances

    start_time = time.time()
    bigger = np.array(find_bigger_img(img1, img2)[0])
    smaller = np.array(find_bigger_img(img1, img2)[1])

    capped = get_resized_image(smaller, bigger)

    sum = 0
    for r in range(len(capped)):
        for c in range(len(capped[r])):
            sum += bgr_distance(smaller[r][c], capped[r][c])

    out = sum / (len(capped) * len(capped[0]))
    time_spent_calculating_distances += (time.time() - start_time)
    return out


def bgr_distance(color1, color2):
    global time_spent_calculating_bgr_distances

    start_time = time.time()
    sum = 0
    for i in range(3):
        sum += math.pow(color1[i] - color2[i], 2)
    # may have to reintroduce square root
    out = math.sqrt(sum)

    time_spent_calculating_bgr_distances += time.time() - start_time
    return out


def average_distance(image_to_compare, correct_images):
    min_distance = 1000000
    for img in correct_images:
        dist = distance(img, image_to_compare)
        if dist < min_distance:
            min_distance = dist
    return min_distance


# To be used directly and in conjunction:

# used to determine the most probable out of multiple ROI, returns ROI image ao
def get_most_accurate(images_to_compare, image_dataset):
    min_distance = 500000000
    out_image_index = 0

    count = 0

    for i in range(len(images_to_compare)):

        img_to_compare = np.array(images_to_compare[i][0])

        # cv.imshow("img_to_compare", np.array(img_to_compare[0]))
        # cv.waitKey(0)
        # time.sleep(10000)

        dist = average_distance(img_to_compare, image_dataset)
        if dist < min_distance:
            min_distance = dist
            out_image_index = i

        count += 1

        # print("count = " + str(count))
        # print(dist)
        #
        # cv.imshow("img-selected" + str(count), img_to_compare)

    return images_to_compare[out_image_index], out_image_index


def get_k_most_accurate(images_to_compare, image_dataset, k):
    out = []

    for i in range(k):
        most_accurate = get_most_accurate(images_to_compare, image_dataset)
        out.append(most_accurate)
        images_to_compare = remove_index(images_to_compare, most_accurate[1])

        if len(images_to_compare) == 0 and i != k - 1:
            print("not enough roi for given Haar Cascade and corresponding feature")

    return out


def remove_index(list, index):
    out = []
    for i in range(len(list)):
        if i != index:
            out.append(list[i])
    return out


# takes the output from detectMultiScaleArray for any HaarCascade
# the return value for this function is the same as images_to_compare in get_most_accurate
def get_all_roi(roi_for_color, feature_plane):
    all_roi = []
    for (x, y, w, h) in feature_plane:
        image = []
        image_pixels = []
        for r in range(y, y + h):
            row = []
            for c in range(x, x + w):
                row.append(roi_for_color[r][c])
            image_pixels.append(row)
        image.append(image_pixels)
        image.append([x, y, w, h])
        all_roi.append(image)
    return all_roi


def get_image_dataset(filepath):
    return load_images_from_folder(filepath)


def main(facialimage_filepath):
    global total_time

    # start_time = time.time()

    face = cv.CascadeClassifier('HaarCascades/haarcascade_frontalface_alt.xml')
    eyes = cv.CascadeClassifier('HaarCascades/haarcascade_eye.xml')
    nose = cv.CascadeClassifier('HaarCascades/haarcascade_mcs_nose.xml')
    mouth = cv.CascadeClassifier('HaarCascades/haarcascade_mcs_mouth.xml')

    img = cv.imread(facialimage_filepath)

    gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    faces = face.detectMultiScale(gray, 1.3, 5)

    # Loop through face in order to find the face,
    # loop within face to find eyes

    for (x, y, w, h) in faces:
        # FEATURES.append([x - x, y - y, w, h])
        # cv.rectangle(img, (x, y), (x + w, y + h), (255, 255, 255), 2)
        # cv.putText(img, "face", (x, y), cv.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255))

        roi_gray = gray[y:y + h, x:x + w]
        roi_color = img[y:y + h, x:x + w]

        # ml
        eyes_plane = eyes.detectMultiScale(roi_gray)
        mp_rois = get_k_most_accurate(get_all_roi(roi_color, eyes_plane), get_image_dataset("eyes"), 2)

        # do not uncomment
        # for i in range(len(mp_rois)):
        #     mp_roi = mp_rois[i][0][1]
        #     cv.rectangle(img, (x + mp_roi[0], y + mp_roi[1]), (x + mp_roi[0] + mp_roi[2], y + mp_roi[1] + mp_roi[3]),
        #                  (255, 255, 0), 2)

        if mp_rois[0][0][1][0] < mp_rois[1][0][1][0]:
            FEATURES.append(mp_rois[1][0][1])
            FEATURES.append(mp_rois[0][0][1])
        elif mp_rois[0][0][1][0] > mp_rois[1][0][1][0]:
            FEATURES.append(mp_rois[0][0][1])
            FEATURES.append(mp_rois[1][0][1])

        # eyes_plane = eyes.detectMultiScale(roi_gray)
        # for (ex, ey, ew, eh) in eyes_plane:
        #     # cv.putText(img, "eye", (ex, ey), cv.FONT_HERSHEY_SIMPLEX, 2, (255, 255, 255))
        #     cv.rectangle(roi_color, (ex, ey), (ex + ew, ey + eh), (0, 255, 0), 2)

        # ml
        nose_plane = nose.detectMultiScale(roi_gray)
        mp_roi = get_most_accurate(get_all_roi(roi_color, nose_plane), get_image_dataset("noses"))[0][1]
        FEATURES.append(mp_roi)
        # cv.rectangle(img, (x + mp_roi[0], y + mp_roi[1]), (x + mp_roi[0] + mp_roi[2], y + mp_roi[1] + mp_roi[3]),
        #              (255, 0, 0), 2)

        # nose_plane = nose.detectMultiScale(roi_gray)
        # for (nx, ny, nw, nh) in nose_plane:
        #     cv.rectangle(roi_color, (nx, ny), (nx + nw, ny + nh), (255, 0, 0), 2)

        # ml
        mouth_plane = mouth.detectMultiScale(roi_gray)
        mp_roi = get_most_accurate(get_all_roi(roi_color, mouth_plane), get_image_dataset("mouths"))[0][1]
        FEATURES.append(mp_roi)
        # cv.rectangle(img, (x + mp_roi[0], y + mp_roi[1]), (x + mp_roi[0] + mp_roi[2], y + mp_roi[1] + mp_roi[3]),
        #              (0, 0, 0), 2)

        # mouth_plane = mouth.detectMultiScale(roi_gray)
        # for (mx, my, mw, mh) in mouth_plane:
        #     cv.rectangle(roi_color, (mx, my), (mx + mw, my + mh), (0, 0, 255), 2)

        FEATURES.append([x, y, w, h])

        # print("all roi: ")
        # for mp_roi in FEATURES:
        #     print(mp_roi)
        #     cv.rectangle(img, (x + mp_roi[0], y + mp_roi[1]), (x + mp_roi[0] + mp_roi[2], y + mp_roi[1] + mp_roi[3]),
        #                  (0, 0, 0), 2)
        # print("", end="\n")

    # analysis
    # total_time = time.time() - start_time
    # print("total time: " + str(total_time))
    # print("time calculating distance: " + str(time_spent_calculating_distances))
    # print("% of total spent on distances: " + str('%.3f' % ((time_spent_calculating_distances / total_time) * 100)))
    # print("time spent calculating bgr distances (within distances): " + str(time_spent_calculating_bgr_distances))
    # print("% of time calculating distances spent on bgr calculations: " + str(
    #     '%.3f' % ((time_spent_calculating_bgr_distances / time_spent_calculating_distances) * 100)))

    # cv.imshow("processed", img)
    # cv.moveWindow("processed", 0, 0)


def absolute_location():
    return os.path.dirname(os.path.abspath("main.py"))


def in_directory():
    path = ""
    for dir in absolute_location().split("/")[0:len(absolute_location().split("/")) - 1]:
        path += dir + "/"
    path += "In/"
    return path


# UNCOMMENT NEXT TWO LINES
main(in_directory() + "original.jpg")
write_features_to_disk()

# cv.waitKey(0)
# time.sleep(100000)

# cap.release()
# cv.destroyAllWindows()
