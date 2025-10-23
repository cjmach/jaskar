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
 * Supported key algorithms.
 * 
 * @author cmachado
 */
public enum KeyAlgorithm {

    /**
     * 128-bit AES-GCM.
     */
    AES_A128_GCM("a128gcm"),
    /**
     * 256-bit AES-GCM.
     */
    AES_A256_GCM("a256gcm"),
    /**
     * 128-bit AES-CBC with HMAC-256.
     */
    AES_A128_CBC_HS256("a128cbchs256"),
    /**
     * 256-bit AES-CBC with HMAC-512.
     */
    AES_A256_CBC_HS512("a256cbchs512"),
    /**
     * 128-bit AES Key Wrap.
     */
    AES_A128_KW("a128kw"),
    /**
     * 256-bit AES Key Wrap.
     */
    AES_A256_KW("a256kw"),
    /**
     * Bls12_381 G1 Curve.
     */
    BLS_12381_G1("bls12381g1"),
    /**
     * Bls12_381 G2 Curve.
     */
    BLS_12381_G2("bls12381g2"),
    /**
     * ChaCha20-Poly1305.
     */
    CHACHA20_C20P("c20p"),
    /**
     * XChaCha20-Poly1305.
     */
    CHACHA20_XC20P("xc20p"),
    /**
     * Ed25519 signing key.
     */
    ED25519("ed25519"),
    /**
     * Curve25519 elliptic curve key exchange key.
     */
    X25519("x25519"),
    /**
     * Koblitz 256 curve.
     */
    EC_SECP_256K1("k256"),
    /**
     * NIST P-256 curve.
     */
    EC_SECP_256R1("p256"),
    /**
     * NIST P-384 curve.
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
