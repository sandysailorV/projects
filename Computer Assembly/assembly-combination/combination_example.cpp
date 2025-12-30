#include <cstdint>
#include <iostream>
using namespace std;

uint16_t factorial(const uint16_t x) {  
    uint16_t fact = 1;
    for (uint16_t i = 1; i <= x; ++i) {
        fact *= i;
    }
    return fact;
}

int main() {
    int n, k;

    // get and validate user input
    cout << "Enter n: ";
    cin >> n;
    cout << "Enter k: ";
    cin >> k;

    // calculate C(n,k) = n! / (k! * (n-k)!)
    uint16_t nFact  = factorial(static_cast<uint16_t>(n));
    uint16_t kFact  = factorial(static_cast<uint16_t>(k));
    uint16_t nkFact = factorial(static_cast<uint16_t>(n - k));

    uint32_t denom = static_cast<uint32_t>(kFact) * nkFact;  // widen to 32-bit
    if (denom == 0) {
        cout << "result = -1" << endl;
        return 0;
    }

    uint16_t c_n_k = static_cast<uint16_t>(nFact / denom);

    // output result
    cout << "result = " << c_n_k << endl;

    return 0;
}