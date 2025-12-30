#include <stdint.h>

#include <bitset>
#include <cmath>
#include <cstdint>
#include <iomanip>
#include <iostream>
#include <limits>

using namespace std;

#define NUM_TESTS 10
#define MAX_VALUE 100
#define MIN_VALUE -100
uint8_t const table_width[] = {12, 12, 35, 12};

// IEEE 754 single-precision float constants
uint8_t const width = 32U; //32 bit float
uint8_t const exp_width = 8U; //8 exponent bits
uint8_t const mantissa_width = width - exp_width - 1; //23 fraction bits
uint8_t const bias = 127U; 


// keep only ONE definition of this function
float ieee_754(uint32_t const data) {
    //Extract from the 32 bit pattern based on global vars
    uint32_t const sign_bit      = (data >> (width - 1)) & 1u;
    uint32_t const exponent_bits = (data >> mantissa_width) & ((1u << exp_width) - 1u);
    uint32_t const fraction_bits =  data & ((1u << mantissa_width) - 1u);

    // Zero exponent
    if (exponent_bits == 0u) {          // means exp bits are all zeros
        if (fraction_bits == 0u) {      // AND frac bits are all zeros
            return (sign_bit == 1u) ? -0.0f : 0.0f; 
        }
    }

    // converts the bits into a small fraction
    float frac_divisor = 8388608.0f; // that’s just 2^23
    float fraction = static_cast<float>(fraction_bits) / frac_divisor;
    float mantissa = 1.0f + fraction; // add the hidden “1” 
    int exponent = static_cast<int>(exponent_bits) - static_cast<int>(bias);

    float value = std::ldexp(mantissa, exponent);
    if (sign_bit == 1u) value = -value;
    return value;

}

void header() {
    cout << left << setw(table_width[0]) << setfill(' ') << "pass/fail";
    cout << left << setw(table_width[1]) << setfill(' ') << "value";
    cout << left << setw(table_width[2]) << setfill(' ') << "bits";
    cout << left << setw(table_width[3]) << setfill(' ') << "IEEE-754" << endl;

    cout << left << setw(table_width[0]) << setfill(' ') << "--------";
    cout << left << setw(table_width[1]) << setfill(' ') << "--------";
    cout << left << setw(table_width[2]) << setfill(' ') << "--------";
    cout << left << setw(table_width[3]) << setfill(' ') << "--------" << endl;
}

void print_row(bool const test_success, float const rand_val, uint32_t const val_int, float const ieee_754_value) {
    // print results
    string const pass_fail = test_success ? "PASS" : "FAIL";
    cout << left << setw(table_width[0]) << setfill(' ') << pass_fail;
    cout << left << setw(table_width[1]) << setfill(' ') << rand_val;
    cout << left << setw(table_width[2]) << setfill(' ') << bitset<width>(val_int);
    cout << left << setw(table_width[3]) << setfill(' ') << ieee_754_value << endl;
}

//Returns a random-value between [min, max] for the test cases
template <typename T>
T rand_min_max(T const min, T const max) {
    T const rand_val =
        min + static_cast<double>(static_cast<double>(rand())) / (static_cast<double>(RAND_MAX / (max - min)));
    return rand_val;
}

//overlays the 4 bytes
bool test() {
    // the union
    union float_uint {
        float val_float;
        uint32_t val_int;
    } data;

    // print header
    header();

    // seed the random number generator
    srand(time(NULL));

    bool success = true;
    uint16_t pass = 0;
    for (size_t i = 0; i < NUM_TESTS; i++) {
        // random value
        float const rand_val = rand_min_max<float>(MIN_VALUE, MAX_VALUE);

        data.val_float = rand_val;

        // calculate using ieee_754 function
        float ieee_754_value = ieee_754(data.val_int);

        // test the results
        float const epsilon = std::numeric_limits<float>::epsilon();
        bool test_success = (abs(ieee_754_value - rand_val) < epsilon);
        if (test_success) {
            pass += 1;
        }

        // print row
        print_row(test_success, rand_val, data.val_int, ieee_754_value);
    }

    // summarize results
    cout << "-------------------------------------------" << endl;
    if (pass == NUM_TESTS) {
        cout << "SUCCESS ";
    } else {
        cout << "FAILURE ";
    }
    cout << pass << "/" << NUM_TESTS << " passed" << endl;
    cout << "-------------------------------------------" << endl;

    return success;
}

int main() {
    if (!test()) {
        return -1;
    }
    return 0;
}