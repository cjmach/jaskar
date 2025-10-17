/*
 *  Copyright 2025 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.jaskar;

/**
 *
 * @author cmachado
 */
public enum KeyAlgorithm {

    /**
     *
     */
    AES_A128_GCM("a128gcm"),
    /**
     *
     */
    AES_A256_GCM("a256gcm"),
    /**
     *
     */
    AES_A128_CBC_HS256("a128cbchs256"),
    /**
     *
     */
    AES_A256_CBC_HS512("a256cbchs512"),
    /**
     *
     */
    AES_A128_KW("a128kw"),
    /**
     *
     */
    AES_A256_KW("a256kw"),
    /**
     *
     */
    BLS_12381_G1("bls12381g1"),
    /**
     *
     */
    BLS_12381_G2("bls12381g2"),
    /**
     *
     */
    CHACHA20_C20P("c20p"),
    /**
     *
     */
    CHACHA20_XC20P("xc20p"),
    /**
     *
     */
    ED25519("ed25519"),
    /**
     *
     */
    X25519("x25519"),
    /**
     *
     */
    EC_SECP_256K1("k256"),
    /**
     *
     */
    EC_SECP_256R1("p256"),
    /**
     *
     */
    EC_SECP_384R1("p384");

    private final String algorithm;

    private KeyAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     *
     * @return
     */
    String getAlgorithm() {
        return algorithm;
    }

    /**
     *
     * @param algorithm
     * @return
     */
    static KeyAlgorithm fromAlgorithm(String algorithm) {
        switch (algorithm) {
            case "a128gcm":
                return AES_A128_GCM;
            case "a256gcm":
                return AES_A256_GCM;
            case "a128cbchs256":
                return AES_A128_CBC_HS256;
            case "a256cbchs512":
                return AES_A256_CBC_HS512;
            case "a128kw":
                return AES_A128_KW;
            case "a256kw":
                return AES_A256_KW;
            case "bls12381g1":
                return BLS_12381_G1;
            case "bls12381g2":
                return BLS_12381_G2;
            case "c20p":
                return CHACHA20_C20P;
            case "xc20p":
                return CHACHA20_XC20P;
            case "ed25519":
                return ED25519;
            case "x25519":
                return X25519;
            case "k256":
                return EC_SECP_256K1;
            case "p256":
                return EC_SECP_256R1;
            case "p384":
                return EC_SECP_384R1;
            default:
                throw new IllegalArgumentException("Unknown algorithm " + algorithm);
        }
    }
}
