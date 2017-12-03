
package baul;
import java.io.ByteArrayOutputStream;
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
	 public static int tamanoHeader;
	 public static int tamOriginal;
   static void fileProcessor(int cipherMode,Key key,File inputFile,File outputFile){
	 try {
	       
	       Cipher cipher = Cipher.getInstance("AES");
	       cipher.init(cipherMode, key);
	       
	       saveKey(key, "claveAES");

	       FileInputStream inputStream = new FileInputStream(inputFile);
	       byte[] inputBytes = new byte[(int) inputFile.length()];
	       inputStream.read(inputBytes);

	       // Aqui cifra el archivo
	       byte[] outputBytes = cipher.doFinal(inputBytes);

	       // Contenido del archivo cifrado en outputBytes
	       // --- antes de escribirlo en el outputFile, hay que concatenarle al principio la clave cifrada
	       
	       // ----->> para ello primero colocamos el metodo que cifra la clave
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
	
		       // Obtener la clave para encriptar/desencriptar
		       
		       File fichero = new File("claveAES");
		       FileInputStream fis = new FileInputStream(fichero);
		       int numBtyes = fis.available();
		       byte[] bytes = new byte[numBtyes];
		       fis.read(bytes);
		       fis.close();	       
		       rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		       rsa.init(Cipher.ENCRYPT_MODE, publicKey);
		       // CLAVE CIFRADA -->> en "encriptado"
		       byte[] encriptado = rsa.doFinal(bytes);
		       
		       // Creamos outputStream que contendra el texto cifrado o descifrado segun el siguiente if
		       FileOutputStream outputStream = new FileOutputStream(outputFile);
		       
				if(cipherMode == Cipher.ENCRYPT_MODE) {
					// Si modo ENCRIPTAR: tenemos que anyadir la clave en la cabecera
				       // Se concatena la clave RSA con el archivo cifrado, en ese orden, para que aparezca en la cabecera

						ByteArrayOutputStream byteoutput = new ByteArrayOutputStream( );
						// Colocamos la clave primero
						byteoutput.write( encriptado );
						// Concatenamos el texto cifrado
						byteoutput.write( outputBytes );
						byte completo[] = byteoutput.toByteArray( );
						
						// Y lo manda al outpuFile que es el encryptedFile (en el main)
				       outputStream.write(completo);
				       
				       // Calculamos el tamano de la clave y el tamano del archivo 
				       // para luego al desencriptar saber cuanto mide cada uno. Solo se
				       // actualizan los valores cada vez que cambia el texto a encriptar
				       tamanoHeader = encriptado.length;
				       tamOriginal = inputBytes.length;
				       
				       

				       //System.out.println("encript head " + tamanoHeader);
				       //System.out.println("encript original " + tamOriginal);
				}
				else {
					// Si modo DESENCRIPTAR: tenemos que quitarle la cabecera y quedarnos con el resto, para ello:
					
					//Array nuevo del mismo tamano del archivo SIN ENCRIPTAR 
					byte[] contenido = new byte[tamOriginal];
					
					// para que contenga la misma informacion que el archivo sin encriptar:
					//	Pasamos valores del array ya desencriptado al nuevo
					// k: indice del header, para saber DONDE EMPIEZA el contenido del archivo real
					for(int i = 0,k = tamanoHeader;i<tamOriginal;i++, k++) {
						contenido[i]=outputBytes[k];
					}
					// Y lo manda al outpuFile que es el desencryptedFile
				       outputStream.write(contenido);

				       File compruebaconcatena = new File("compruebaconcatena.txt");
				       FileOutputStream outputStream3 = new FileOutputStream(compruebaconcatena);
				       outputStream3.write(outputBytes);
				       
				       //System.out.println("decript head " + tamanoHeader);
				       //System.out.println("decript original " + tamOriginal);
				}
	       

	       File salida = new File("claveRSA.txt");
	       FileOutputStream outputStream2 = new FileOutputStream(salida);
	       outputStream2.write(encriptado);
	       //aqui ya he encriptado la clave de aes con rsa y la tengo en un fichero
	       
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
    
    String nombre = "fichero.txt";
    // Obtener primero el nombre del archivo (para cuando se haga el selector de archivo a cifrar)
    	// TODO
    // Se carga (con el nombre anterior)
	File inputFile = new File(nombre);//aqui getFiechero
	
	//if(true){//cambiar para que esto se haga cuando se active la opcion encriptar
	// Se crea archivo - que sera el encriptado	
	File encryptedFile = new File(inputFile.getName()+"-ENCRIPTED.txt");
		
	//}
	
	//else if(false){//esto se hace cuando elijamos desencriptar y TENGA LA CLAVE
	// Se crea archivo - que sera el desencriptado	
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
