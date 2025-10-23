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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import pt.cjmach.jaskar.lib.AeadParams;
import pt.cjmach.jaskar.lib.AskarLibrary;
import pt.cjmach.jaskar.lib.ByteBuffer;
import pt.cjmach.jaskar.lib.EncryptedBuffer;
import pt.cjmach.jaskar.lib.ErrorCode;
import pt.cjmach.jaskar.lib.SecretBuffer;
import pt.cjmach.jaskar.lib.StringList;

/**
 *
 * @author cmachado
 */
public class Key implements Closeable {

    Pointer handle;

    /**
     * 
     * @param handle 
     */
    Key(Pointer handle) {
        this.handle = handle;
    }

    /**
     * Perform AEAD message decryption with this encryption key.
     * 
     * @param cipherText
     * @param nonce
     * @param tag
     * @param aad
     * @return
     * @throws AskarException 
     */
    public byte[] aeadDecrypt(byte[] cipherText, byte[] nonce, byte[] tag, byte[] aad) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue cipherTextBuffer = new ByteBuffer.ByValue(cipherText); ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce); ByteBuffer.ByValue tagBuffer = new ByteBuffer.ByValue(tag); ByteBuffer.ByValue aadBuffer = new ByteBuffer.ByValue(aad)) {
            ErrorCode errorCode = AskarLibrary.askar_key_aead_decrypt(handle, cipherTextBuffer, nonceBuffer, tagBuffer, aadBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            return out.getBytes();
        }
    }
    
    public byte[] aeadDecrypt(WrappedSecret key, byte[] aad) throws AskarException {
        return aeadDecrypt(key.getCiphertext(), key.getNonce(), key.getTag(), aad);
    }

    /**
     * Perform AEAD message encryption with this encryption key.
     * 
     * @param message
     * @param nonce
     * @param aad
     * @return
     * @throws AskarException 
     */
    public WrappedSecret aeadEncrypt(byte[] message, byte[] nonce, byte[] aad) throws AskarException {
        try (ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce)) {
            return aeadEncrypt(message, nonceBuffer, aad);
        }
    }
    
    public WrappedSecret aeadEncrypt(byte[] message, byte[] aad) throws AskarException {
        try (ByteBuffer.ByValue nonce = new ByteBuffer.ByValue()) {
            return aeadEncrypt(message, nonce, aad);
        }
    }
    
    private WrappedSecret aeadEncrypt(byte[] message, ByteBuffer.ByValue nonce, byte[] aad) throws AskarException {
        try (EncryptedBuffer out = new EncryptedBuffer(); ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message); ByteBuffer.ByValue aadBuffer = new ByteBuffer.ByValue(aad)) {
            ErrorCode errorCode = AskarLibrary.askar_key_aead_encrypt(handle, messageBuffer, nonce, aadBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            WrappedSecret wrapped = new WrappedSecret(out);
            return wrapped;
        }
    }

    /**
     * 
     */
    @Override
    public void close() {
        if (handle != Pointer.NULL) {
            AskarLibrary.askar_key_free(handle);
            handle = Pointer.NULL;
        }
    }

    /**
     * Map this key or keypair to its equivalent for another key algorithm.
     * 
     * @param algorithm
     * @return
     * @throws AskarException 
     */
    public Key convert(KeyAlgorithm algorithm) throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_convert(handle, algorithm.getAlgorithm(), out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        Key key = new Key(p);
        return key;
    }

    /**
     * Derive a new key from a Diffie-Hellman exchange between this keypair and a public key.
     * 
     * @param algorithm
     * @param publicKey
     * @return
     * @throws AskarException 
     */
    public Key fromKeyExchange(KeyAlgorithm algorithm, Key publicKey) throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_from_key_exchange(algorithm.getAlgorithm(), handle, publicKey.handle, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        Key key = new Key(p);
        return key;
    }

    /**
     * Fetch the AEAD parameter lengths.
     * 
     * @return
     * @throws AskarException 
     */
    public AeadParams getAeadParams() throws AskarException {
        AeadParams params = new AeadParams();
        ErrorCode errorCode = AskarLibrary.askar_key_aead_get_params(handle, params);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        return params;
    }

    /**
     * Create a new random nonce for AEAD message encryption.
     * 
     * @return
     * @throws AskarException 
     */
    public byte[] getAeadRandomNonce() throws AskarException {
        try (SecretBuffer buffer = new SecretBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_aead_random_nonce(handle, buffer);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] nonce = buffer.getBytes();
            return nonce;
        }
    }

    /**
     * Gets the key algorithm.
     * 
     * @return
     * @throws AskarException 
     */
    public KeyAlgorithm getAlgorithm() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_get_algorithm(handle, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String algorithm = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        KeyAlgorithm keyAlgo = KeyAlgorithm.fromAlgorithm(algorithm);
        return keyAlgo;
    }

    /**
     * Get the JWK thumbprint for this key or keypair.
     * 
     * @return
     * @throws AskarException 
     */
    public String getJwkThumbprint() throws AskarException {
        KeyAlgorithm algorithm = getAlgorithm();
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_get_jwk_thumbprint(handle, algorithm.getAlgorithm(), out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String result = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return result;
    }

    /**
     * Export the raw bytes of the public key.
     * 
     * @return
     * @throws AskarException 
     */
    public byte[] getPublicBytes() throws AskarException {
        try (SecretBuffer buffer = new SecretBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_get_public_bytes(handle, buffer);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] bytes = buffer.getBytes();
            return bytes;
        }
    }

    /**
     * Get the public JWK representation for this key or keypair.
     * 
     * @return
     * @throws AskarException 
     */
    public String getPublicJwk() throws AskarException {
        KeyAlgorithm algorithm = getAlgorithm();
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_get_jwk_public(handle, algorithm.getAlgorithm(), out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        String jwk = p.getString(0, AskarLibrary.DEFAULT_CHARSET.name());
        Native.free(Pointer.nativeValue(p));
        return jwk;
    }

    /**
     * Export the raw bytes of the private key.
     * 
     * @return
     * @throws AskarException 
     */
    public byte[] getSecretBytes() throws AskarException {
        try (SecretBuffer buffer = new SecretBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_get_secret_bytes(handle, buffer);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] bytes = buffer.getBytes();
            return bytes;
        }
    }

    /**
     * Get the JWK representation for this private key or keypair.
     * 
     * @return
     * @throws AskarException 
     */
    public byte[] getSecretJwk() throws AskarException {
        try (SecretBuffer buffer = new SecretBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_get_jwk_secret(handle, buffer);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] jwk = buffer.getBytes();
            return jwk;
        }
    }

    /**
     * 
     * @return
     * @throws AskarException 
     */
    public boolean isEphemeral() throws AskarException {
        ByteByReference out = new ByteByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_get_ephemeral(handle, out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        byte ephemeral = out.getValue();
        return ephemeral != 0;
    }

    /**
     * Sign a message with this private signing key.
     * 
     * @param message
     * @param algorithm
     * @return
     * @throws AskarException 
     */
    public byte[] signMessage(byte[] message, SignatureAlgorithm algorithm) throws AskarException {
        return signMessage(message, algorithm.getAlgorithm());
    }
    
    public byte[] signMessage(byte[] message) throws AskarException {
        return signMessage(message, (String) null);
    }
    
    private byte[] signMessage(byte[] message, String algorithm) throws AskarException {
        try (SecretBuffer out = new SecretBuffer(); ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message)) {
            ErrorCode errorCode = AskarLibrary.askar_key_sign_message(handle, messageBuffer, algorithm, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte[] signature = out.getBytes();
            return signature;
        }
    }
    
    /**
     * Unwrap a key using this key.
     * 
     * @param algorithm
     * @param cipherText
     * @param nonce
     * @param tag
     * @return
     * @throws AskarException 
     */
    public Key unwrapKey(KeyAlgorithm algorithm, byte[] cipherText, byte[] nonce, byte[] tag) throws AskarException {
        try (ByteBuffer.ByValue cipherTextBuffer = new ByteBuffer.ByValue(cipherText);
                ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce);
                ByteBuffer.ByValue tagBuffer = new ByteBuffer.ByValue(tag)) {
            PointerByReference out = new PointerByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_unwrap_key(handle, algorithm.getAlgorithm(), cipherTextBuffer, nonceBuffer, tagBuffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            Pointer p = out.getValue();
            Key key = new Key(p);
            return key;
        }
    }
    
    public Key unwrapKey(KeyAlgorithm algorithm, WrappedSecret wrapped) throws AskarException {
        return unwrapKey(algorithm, wrapped.getCiphertext(), wrapped.getNonce(), wrapped.getTag());
    }

    /**
     * Verify a message signature with this private signing key or public verification key.
     * 
     * @param message
     * @param signature
     * @param algorithm
     * @return
     * @throws AskarException 
     */
    public boolean verifySignature(byte[] message, byte[] signature, SignatureAlgorithm algorithm) throws AskarException {
        return verifySignature(message, signature, algorithm.getAlgorithm());
    }
    
    public boolean verifySignature(byte[] message, byte[] signature) throws AskarException {
        return verifySignature(message, signature, (String) null);
    }
    
    private boolean verifySignature(byte[] message, byte[] signature, String algorithm) throws AskarException {
        try (ByteBuffer.ByValue messageBuffer = new ByteBuffer.ByValue(message); ByteBuffer.ByValue signatureBuffer = new ByteBuffer.ByValue(signature)) {
            ByteByReference out = new ByteByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_verify_signature(handle, messageBuffer, signatureBuffer, algorithm, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            byte verified = out.getValue();
            return verified != 0;
        }
    }

    /**
     * Wrap another key using this key.
     * 
     * @param other
     * @param nonce
     * @return
     * @throws AskarException 
     */
    public WrappedSecret wrapKey(Key other, byte[] nonce) throws AskarException {
        try (ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue(nonce)) {
            return wrapKey(other, nonceBuffer);
        }
    }
    
    public WrappedSecret wrapKey(Key other) throws AskarException {
        try (ByteBuffer.ByValue nonceBuffer = new ByteBuffer.ByValue()) {
            return wrapKey(other, nonceBuffer);
        }
    }
    
    private WrappedSecret wrapKey(Key other, ByteBuffer.ByValue nonce) throws AskarException {
        try (EncryptedBuffer out = new EncryptedBuffer()) {
            ErrorCode errorCode = AskarLibrary.askar_key_wrap_key(handle, other.handle, nonce, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            WrappedSecret key = new WrappedSecret(out);
            return key;
        }
    }

    /**
     * Import a key or keypair from a JWK. Only ECDSA and EdDSA based JWKs are 
     * supported. Example:
     * <pre>
     * {
     *   "kty": "EC",
     *   "crv": "P-256",
     *   "kid": "50825016500706350612321887056575493243659129373",
     *   "x": "oysGbyjGZGT-TqQ6Fg2WOduSY2CU1ScjR4g6_DZ41lo",
     *   "y": "-fTBlzT2CmJiFEnRQGyW_2O_dEwgYyZ-AUCY_Ate1eI"
     * }
     * </pre>
     *
     * @param jwk
     * @return
     * @throws AskarException
     */
    public static Key fromJwk(String jwk) throws AskarException {
        return fromJwk(jwk.getBytes(AskarLibrary.DEFAULT_CHARSET));
    }

    /**
     * Import a key or keypair from a JWK in binary format. Only ECDSA and EdDSA 
     * based JWKs are supported.
     *
     * @param jwk
     * @return
     * @throws AskarException
     */
    public static Key fromJwk(byte[] jwk) throws AskarException {
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(jwk)) {
            PointerByReference out = new PointerByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_from_jwk(buffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            Pointer p = out.getValue();
            Key key = new Key(p);
            return key;
        }
    }

    /**
     * Import a public key from its compact representation.
     * 
     * @param algorithm
     * @param publicKey
     * @return
     * @throws AskarException 
     */
    public static Key fromPublicBytes(KeyAlgorithm algorithm, byte[] publicKey) throws AskarException {
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(publicKey)) {
            PointerByReference out = new PointerByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_from_public_bytes(algorithm.getAlgorithm(), buffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            Pointer p = out.getValue();
            Key key = new Key(p);
            return key;
        }
    }

    /**
     * Import a symmetric key or public-private keypair from its compact representation.
     * 
     * @param algorithm
     * @param secretKey
     * @return
     * @throws AskarException 
     */
    public static Key fromSecretBytes(KeyAlgorithm algorithm, byte[] secretKey) throws AskarException {
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(secretKey)) {
            PointerByReference out = new PointerByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_from_secret_bytes(algorithm.getAlgorithm(), buffer, out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            Pointer p = out.getValue();
            Key key = new Key(p);
            return key;
        }
    }

    /**
     * Create a new deterministic key or keypair.
     * 
     * @param method
     * @param algorithm
     * @param seed
     * @return
     * @throws AskarException 
     */
    public static Key fromSeed(KeyMethod method, KeyAlgorithm algorithm, String seed) throws AskarException {
        try (ByteBuffer.ByValue buffer = new ByteBuffer.ByValue(seed)) {
            PointerByReference out = new PointerByReference();
            ErrorCode errorCode = AskarLibrary.askar_key_from_seed(algorithm.getAlgorithm(), buffer, method.getMethod(), out);
            if (errorCode != ErrorCode.SUCCESS) {
                throw new AskarException();
            }
            Pointer p = out.getValue();
            Key key = new Key(p);
            return key;
        }
    }

    /**
     * Create a new random key or keypair.
     * 
     * @param algorithm
     * @param backend
     * @param ephemeral
     * @return
     * @throws AskarException 
     */
    public static Key generate(KeyAlgorithm algorithm, KeyBackend backend, boolean ephemeral) throws AskarException {
        return generate(algorithm, backend.getBackend(), ephemeral);
    }
    
    public static Key generate(KeyAlgorithm algorithm, boolean ephemeral) throws AskarException {
        return generate(algorithm, (String) null, ephemeral);
    }
    
    private static Key generate(KeyAlgorithm algorithm, String backend, boolean ephemeral) throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_generate(algorithm.getAlgorithm(), backend, (byte) (ephemeral ? 1 : 0), out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        Key key = new Key(p);
        return key;
    }
    
    /**
     * 
     * @return
     * @throws AskarException 
     */
    public static KeyBackend[] getSupportedBackends() throws AskarException {
        PointerByReference out = new PointerByReference();
        ErrorCode errorCode = AskarLibrary.askar_key_get_supported_backends(out);
        if (errorCode != ErrorCode.SUCCESS) {
            throw new AskarException();
        }
        Pointer p = out.getValue();
        try (StringList list = new StringList(p)) {
            String[] backends = list.toArray();
            KeyBackend[] keyBackends = new KeyBackend[backends.length];
            for (int i = 0; i < backends.length; i++) {
                keyBackends[i] = KeyBackend.fromBackend(backends[i]);
            }
            return keyBackends;
        }
    }
}
