
#include <jni.h>
#include <stdlib.h>
//#include <android/log.h>

//#define APPNAME "PhotoMotion"

typedef struct {
  int r;
  int b;
  int g;
} Rgb;

Rgb ToRgb(jbyte* data, int i1, int i2, int i3) {
    int y = (0xff & ((int) data[i1]));
    int v = (0xff & ((int) data[i2]));
    int u = (0xff & ((int) data[i3]));
    y = y < 16 ? 16 : y;

    int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
    int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
    int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

    r = r < 0 ? 0 : (r > 255 ? 255 : r);
    g = g < 0 ? 0 : (g > 255 ? 255 : g);
    b = b < 0 ? 0 : (b > 255 ? 255 : b);

    Rgb result;
    result.r = r;
    result.g = g;
    result.b = b;

    return result;
}

int CompareRgb(Rgb first, Rgb second, int pixel_threshold) {
    if(abs(first.r - second.r) >= pixel_threshold)
        return 1;
    if(abs(first.g - second.g) >= pixel_threshold)
        return 1;
    if(abs(first.b - second.b) >= pixel_threshold)
        return 1;
    return 0;
}

jboolean JNICALL Java_com_techq_PhotoMotion_data_Detector_Compare(JNIEnv * env,
                                                            jobject obj,
                                                            jbyteArray firstA,
                                                            jbyteArray secondA,
                                                            jint width,
                                                            jint height,
                                                            jint pixel_threshold,
                                                            jint picture_threshold)
{
    int frameSize = width * height;
    int totalPixels = 0;
    jbyte* first = firstA;
    jbyte* second = secondA;

    int j = 0;
    int i = 0;
    int ii = 0;
    int ij = 0;
    int di = +1;
    int dj = +1;
    int ci = 0;
    int cj = 0;

    int i1 = 0;
    int i2 = 0;
    int i3 = 0;

    for(i = 0, ci = ii; i < height;  ++i, ci += di) {
        for(j = 0, cj = ij; j < width; ++j, cj += dj) {

            i1 = ci * width + cj;
            i2 = frameSize + (ci >> 1) * width + (cj & ~1) + 0;
            i3 = frameSize + (ci >> 1) * width + (cj & ~1) + 1;

            if (CompareRgb(ToRgb(first, i1, i2, i3), ToRgb(second, i1, i2, i3), pixel_threshold) != 0)  {
                if (totalPixels++ >= picture_threshold) {
                    return 1;
                }
            }
        }
    }
    return 0;
}

jint JNICALL Java_com_techq_PhotoMotion_data_Detector_CompareAndResult(JNIEnv * env,
                                                            jobject obj,
                                                            jbyteArray firstA,
                                                            jbyteArray secondA,
                                                            jint width,
                                                            jint height,
                                                            jint pixel_threshold)
{
    int frameSize = width * height;
    int totalPixels = 0;
    jbyte* first = firstA;
    jbyte* second = secondA;

    int j = 0;
    int i = 0;
    int ii = 0;
    int ij = 0;
    int di = +1;
    int dj = +1;
    int ci = 0;
    int cj = 0;

    int i1 = 0;
    int i2 = 0;
    int i3 = 0;

    for(i = 0, ci = ii; i < height;  ++i, ci += di) {
        for(j = 0, cj = ij; j < width; ++j, cj += dj) {

            i1 = ci * width + cj;
            i2 = frameSize + (ci >> 1) * width + (cj & ~1) + 0;
            i3 = frameSize + (ci >> 1) * width + (cj & ~1) + 1;

            if (CompareRgb(ToRgb(first, i1, i2, i3), ToRgb(second, i1, i2, i3), pixel_threshold) != 0)  {
                totalPixels++;
            }
        }
    }
    return totalPixels;
}
