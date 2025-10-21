/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pt.cjmach.jaskar;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pt.cjmach.jaskar.lib.AskarLibrary;

/**
 *
 * @author mach
 */
public class KeyTests {
    
    @Test
    public void givenValidKey_whenSigningMessage_thenVerifiesSignature() {
        try (Key keyPair = Key.generate(KeyAlgorithm.EC_SECP_256R1, false)) {
            byte[] message = "message".getBytes(AskarLibrary.DEFAULT_CHARSET);
            byte[] signature = keyPair.signMessage(message);
            assertTrue(keyPair.verifySignature(message, signature), "Error verifying signature");
            
            byte[] badInput = "bad input".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(badInput, signature));
            
            badInput = "xt19s1sp2UZCGhy9rNyb1FtxdKiDGZZPNFnc1KiM9jYYEuHxuwNeFf1oQKsn8zv6yvYBGhXa83288eF4MqN1oDq".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(badInput, signature));
            
            byte[] badSignature = "bad signature".getBytes(AskarLibrary.DEFAULT_CHARSET);
            assertFalse(keyPair.verifySignature(message, badSignature));
            
            Throwable cause = assertThrows(AskarException.class, () -> keyPair.verifySignature(message, signature, SignatureAlgorithm.ES384));
            assertNotNull(cause);
        } catch (AskarException ex) {
            fail(ex);
        }
        
    }
}
