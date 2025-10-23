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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.Objects;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ByteBuffer;
import pt.cjmach.jaskar.lib.ErrorCode;

/**
 *
 * @author cmachado
 */
public class Ecdh1Pu implements Closeable {

    protected final ByteBuffer.ByValue algId;
    protected final ByteBuffer.ByValue apu;
    protected final ByteBuffer.ByValue apv;

    protected Ecdh1Pu(KeyAlgorithm algId, byte[] apu, byte[] apv) {
        Objects.requireNonNull(algId);
        Objects.requireNonNull(apu);
        Objects.requireNonNull(apv);

        this.algId = new ByteBuffer.ByValue(algId.getAlgorithm());
        this.apu = new ByteBuffer.ByValue(apu);
        this.apv = new ByteBuffer.ByValue(apv);
    }

    @Override
    public void close() {
        algId.close();
        apu.close();
        apv.close();
    }
    
    public byte[] decryptDirect(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, 
            byte[] cipherText, byte[] nonce, byte[] tag, byte[] aad) throws AskarException {
        try (Key derived = deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, true)) {
            byte[] decrypted = derived.aeadDecrypt(cipherText, nonce, tag, aad);
            return decrypted;
        }        
    }
    
    public byte[] decryptDirect(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, 
            WrappedSecret wrappedKey, byte[] aad) throws AskarException {
        try (Key derived = deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, true)) {
            byte[] decrypted = derived.aeadDecrypt(wrappedKey, aad);
            return decrypted;
        }        
    }

    protected Key deriveKey(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, boolean receive) throws AskarException {
        try (ByteBuffer.ByValue ccTag = new ByteBuffer.ByValue()) {
            return deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, receive, ccTag);
        }
    }

    Key deriveKey(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, boolean receive, byte[] ccTag) throws AskarException {
        try (ByteBuffer.ByValue ccTagBuffer = new ByteBuffer.ByValue(ccTag)) {
            return deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, receive, ccTagBuffer);
        }
    }

    Key deriveKey(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, boolean receive, ByteBuffer.ByValue ccTag) throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_derive_ecdh_1pu(encryptionAlgorithm.getAlgorithm(),
                ephemeralKey.handle, senderKey.handle, recipientKey.handle, algId, apu, apv, ccTag, (byte) (receive ? 1 : 0), out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        Key key = new Key(p);
        return key;
    }
    
    public WrappedSecret encryptDirect(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, 
            byte[] message, byte[] aad, byte[] nonce) throws AskarException {
        try (Key derived = deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, false)) {
            WrappedSecret encrypted = derived.aeadEncrypt(message, nonce, aad);
            return encrypted;
        }
    }
    
    public WrappedSecret encryptDirect(KeyAlgorithm encryptionAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, 
            byte[] message, byte[] aad) throws AskarException {
        try (Key derived = deriveKey(encryptionAlgorithm, ephemeralKey, senderKey, recipientKey, false)) {
            WrappedSecret encrypted = derived.aeadEncrypt(message, aad);
            return encrypted;
        }
    }
    
    public Key unwrapKey(KeyAlgorithm keyWrappingAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, 
            KeyAlgorithm encryptionAlgorithm, WrappedSecret wrappedKey, byte[] ccTag) throws AskarException {
        try (Key derived = deriveKey(keyWrappingAlgorithm, ephemeralKey, senderKey, recipientKey, true, ccTag)) {
            Key unwrapped = derived.unwrapKey(encryptionAlgorithm, wrappedKey);
            return unwrapped;
        }
    }
    
    public WrappedSecret wrapKey(KeyAlgorithm keyWrappingAlgorithm, Key ephemeralKey, Key senderKey, Key recipientKey, Key cek, byte[] ccTag) throws AskarException {
        try (Key derived = deriveKey(keyWrappingAlgorithm, ephemeralKey, senderKey, recipientKey, false, ccTag)) {
            WrappedSecret wrapped = derived.wrapKey(cek);
            return wrapped;
        }
    }
}
