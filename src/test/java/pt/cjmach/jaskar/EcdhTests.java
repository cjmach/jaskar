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

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author cmachado
 */
public class EcdhTests {

    @Test
    public void givenMessageAndEcdhEs_whenEncryptedAndDecrypted_thenDecryptedIsEqualToMessage() {
        byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
        byte[] apu = "Alice".getBytes(AskarLibrary.DEFAULT_CHARSET);
        byte[] apv = "Bob".getBytes(AskarLibrary.DEFAULT_CHARSET);
        KeyAlgorithm encryptionAlgorithm = KeyAlgorithm.AES_A256_GCM;
        byte[] aad = UUID.randomUUID().toString().getBytes(AskarLibrary.DEFAULT_CHARSET);

        try (Key bobKey = Key.generate(KeyAlgorithm.EC_SECP_256R1, true); Key ephemeralKey = Key.generate(KeyAlgorithm.EC_SECP_256R1, true); EcdhEs ecdhes = new EcdhEs(encryptionAlgorithm, apu, apv)) {
            String bobJwk = bobKey.getPublicJwk();
            String ephemeralJwk = ephemeralKey.getPublicJwk();

            WrappedSecret encryptedMessage;
            try (Key bobPublicKey = Key.fromJwk(bobJwk)) {
                encryptedMessage = ecdhes.encryptDirect(encryptionAlgorithm, ephemeralKey, bobPublicKey, message, aad);
            }
            byte[] messageReceived;
            try (Key ephemeralPublicKey = Key.fromJwk(ephemeralJwk)) {
                messageReceived = ecdhes.decryptDirect(encryptionAlgorithm, ephemeralPublicKey, bobKey, encryptedMessage, aad);
            }
            assertArrayEquals(message, messageReceived);
        } catch (AskarException ex) {
            fail(ex);
        }
    }
    
    @Test
    public void givenMessageAndEcdh1PU_whenEncryptedAndDecrypted_thenDecryptedIsEqualToMessage() {
        byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
        byte[] apu = "Alice".getBytes(AskarLibrary.DEFAULT_CHARSET);
        byte[] apv = "Bob".getBytes(AskarLibrary.DEFAULT_CHARSET);
        KeyAlgorithm encryptionAlgorithm = KeyAlgorithm.AES_A256_GCM;
        byte[] aad = UUID.randomUUID().toString().getBytes(AskarLibrary.DEFAULT_CHARSET);

        try (Key aliceKey = Key.generate(KeyAlgorithm.EC_SECP_256R1, true); Key bobKey = Key.generate(KeyAlgorithm.EC_SECP_256R1, true); Key ephemeralKey = Key.generate(KeyAlgorithm.EC_SECP_256R1, true); Ecdh1Pu ecdh1pu = new Ecdh1Pu(encryptionAlgorithm, apu, apv)) {
            String aliceJwk = aliceKey.getPublicJwk();
            String bobJwk = bobKey.getPublicJwk();

            WrappedSecret encryptedMessage;
            try (Key bobPublicKey = Key.fromJwk(bobJwk)) {
                encryptedMessage = ecdh1pu.encryptDirect(encryptionAlgorithm, ephemeralKey, aliceKey, bobPublicKey, message, aad);
            }
            byte[] messageReceived;
            try (Key alicePublicKey = Key.fromJwk(aliceJwk)) {
                messageReceived = ecdh1pu.decryptDirect(encryptionAlgorithm, ephemeralKey, alicePublicKey, bobKey, encryptedMessage, aad);
            }
            assertArrayEquals(message, messageReceived);
        } catch (AskarException ex) {
            fail(ex);
        }
    }
}
