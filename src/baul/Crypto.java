
package baul;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class Crypto {
	 private static Cipher rsa;
   static void fileProcessor(int cipherMode,Key key,File inputFile,File outputFile){
	 try {
	       
	       Cipher cipher = Cipher.getInstance("AES");
	       cipher.init(cipherMode, key);
	       
	       saveKey(key, "claveAES");

	       FileInputStream inputStream = new FileInputStream(inputFile);
	       byte[] inputBytes = new byte[(int) inputFile.length()];
	       inputStream.read(inputBytes);

	       byte[] outputBytes = cipher.doFinal(inputBytes);

	       FileOutputStream outputStream = new FileOutputStream(outputFile);
	       outputStream.write(outputBytes);

	      
	       
	       
//hasta aqui me ha cifrado el archivo en aes y me guarda la clave aes en un archivo
//que ahora tengo que cifrar
	       
	       // Generar el par de claves
	       KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	       KeyPair keyPair = keyPairGenerator.generateKeyPair();
	       PublicKey publicKey = keyPair.getPublic();
	       PrivateKey privateKey = keyPair.getPrivate();

	       // Se salva y recupera de fichero la clave publica
	       saveKey(publicKey, "publickey.dat");
	       publicKey = loadPublicKey("publickey.dat");

	       // Se salva y recupera de fichero la clave privada
	       saveKey(privateKey, "privatekey.dat");
	       privateKey = loadPrivateKey("privatekey.dat");

	       // Obtener la clase para encriptar/desencriptar
	       
	       File fichero = new File("claveAES");
	       FileInputStream fis = new FileInputStream(fichero);
	       int numBtyes = fis.available();
	       byte[] bytes = new byte[numBtyes];
	       fis.read(bytes);
	       fis.close();	       
	       rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding"); 
	      
	       rsa.init(Cipher.ENCRYPT_MODE, publicKey);
	       byte[] encriptado = rsa.doFinal(bytes);
	      
	       File salida = new File("claveRSA");
	       
	       FileOutputStream outputStream2 = new FileOutputStream(salida);
	       outputStream2.write(encriptado);
	       //aqui ya he encriptado la clave de aes con rsa y la tengo en un fichero
	       
	       //ahora tengo que concatenar el archivo con la clave en rsa del usuario	       
	      
	       
	       outputStream.close();
	       inputStream.close();
	       

	    } catch (Exception e) {
		e.printStackTrace();
            }
     }
	
     public static void main(String[] args) {	
    	 try {
	KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(128);
    Key key = keyGenerator.generateKey();
    
	File inputFile = new File("fichero.txt");//aqui getFiechero
	
	//if(true){//cambiar para que esto se haga cuando se active la opcion encriptar
		File encryptedFile = new File(inputFile.getName()+"-ENCRIPTED.txt");
		
	//}
	
	//else if(false){//esto se hace cuando elijamos desencriptar y TENGA LA CLAVE
		
		File decryptedFile = new File(inputFile.getName()+"-DECRIPTED.txt");
	//}
	
	//ahora tengo que encryptar el archivo que tengo con la clave aes
	
	
	
	
	     Crypto.fileProcessor(Cipher.ENCRYPT_MODE,key,inputFile,encryptedFile);
	     Crypto.fileProcessor(Cipher.DECRYPT_MODE,key,encryptedFile,decryptedFile);
	     System.out.println("Success");
	 } catch (Exception ex) {
	     System.out.println(ex.getMessage());
             ex.printStackTrace();
	 }
   }
     
    private static PublicKey loadPublicKey(String fileName) throws Exception {
         FileInputStream fis = new FileInputStream(fileName);
         int numBtyes = fis.available();
         byte[] bytes = new byte[numBtyes];
         fis.read(bytes);
         fis.close();

         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         KeySpec keySpec = new X509EncodedKeySpec(bytes);
         PublicKey keyFromBytes = keyFactory.generatePublic(keySpec);
         return keyFromBytes;
      }

      private static PrivateKey loadPrivateKey(String fileName) throws Exception {
         FileInputStream fis = new FileInputStream(fileName);
         int numBtyes = fis.available();
         byte[] bytes = new byte[numBtyes];
         fis.read(bytes);
         fis.close();

         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         KeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
         PrivateKey keyFromBytes = keyFactory.generatePrivate(keySpec);
         return keyFromBytes;
      }

      private static void saveKey(Key key, String fileName) throws Exception {
         byte[] publicKeyBytes = key.getEncoded();
         FileOutputStream fos = new FileOutputStream(fileName);
         fos.write(publicKeyBytes);
         fos.close();
      }
}
