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

import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ByteBuffer;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SecretBuffer;

/**
 *
 * @author cmachado
 */
public final class CryptoBox {

    /**
     * 
     */
    private CryptoBox() {
    }

    /**
     * 
     * @param recipientKey
     * @param senderKey
     * @param message
     * @param nonce
     * @return
     * @throws AskarException 
     */
    public static byte[] cryptoBox(Key recipientKey, Key senderKey, byte[] message, byte[] nonce) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message); ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce)) {
            ErrorCode errorCode = AskarLibrary.askar_key_crypto_box(recipientKey.handle, senderKey.handle, messageBuffer, nonceBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] box = out.getBytes();
            return box;
        }
    }
    
    /**
     * 
     * @param recipientKey
     * @param senderKey
     * @param message
     * @param nonce
     * @return
     * @throws AskarException 
     */
    public static byte[] open(Key recipientKey, Key senderKey, byte[] message, byte[] nonce) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message); ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce)) {
            ErrorCode errorCode = AskarLibrary.askar_key_crypto_box_open(recipientKey.handle, senderKey.handle, messageBuffer, nonceBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] box = out.getBytes();
            return box;
        }
    }
    
    /**
     * 
     * @return
     * @throws AskarException 
     */
    public static byte[] randomNonce() throws AskarException {
        try (SecretBuffer out = new SecretBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_crypto_box_random_nonce(out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] nonce = out.getBytes();
            return nonce;
        }
    }
    
    /**
     * 
     * @param key
     * @param message
     * @return
     * @throws AskarException 
     */
    public static byte[] seal(Key key, byte[] message) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message)) {
            ErrorCode errorCode = AskarLibrary.askar_key_crypto_box_seal(key.handle, messageBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] result = out.getBytes();
            return result;
        }
    }
    
    /**
     * 
     * @param key
     * @param ciphertext
     * @return
     * @throws AskarException 
     */
    public static byte[] sealOpen(Key key, byte[] ciphertext) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue ciphertextBuffer = new ByteBuffer.ByValue(ciphertext)) {
            ErrorCode errorCode = AskarLibrary.askar_key_crypto_box_seal_open(key.handle, ciphertextBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] result = out.getBytes();
            return result;
        }
    }
}
