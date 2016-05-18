package ru.mipt.physics.birefringence

/**
 * Created by darksnake on 18-May-16.
 */
class Data {
    double a;
    double aErr = 1
    Vector phi1;
    double phi1Err = 1
    Vector psio;
    double psioErr = 1
    Vector psie;
    double psieErr = 1

    Vector phi2o() {
        return psio - phi1 + a
    }

    Vector phi2e() {
        return psie - phi1 + a
    }

    int size() {
        return phi1.size()
    }
}
