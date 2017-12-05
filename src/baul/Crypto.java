
package baul;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import java.io.FileNotFoundException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
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
	 public static int tamOriginal;
   
   
   static void decrypt(File completo, Key privateKey){
	  try{ 
		  
	   int tamClave=0;
	  	   
	   //Carga archivo cifrado en fis
	   FileInputStream fis = new FileInputStream(completo);
       int numBtyes = fis.available();
       byte[] bytes = new byte[numBtyes];
	   //Se guarda en bytes[] el contenido
       fis.read(bytes);
       fis.close();
       
       //tamClave = bytes[0];
       byte[] clave = new byte[16];
            
       //Guarda el tamaño de la clave () 
       for(int i= 0; i<16;i++) {
			clave[i] = bytes[i];
			//System.out.println((char)clave[i]);
		}
       //System.out.println(clave.length);
       
       int tamFichero;
      // System.out.println(numBtyes);
       tamFichero = numBtyes-16;// - 1;//-tamClave;   
       //System.out.println(tamFichero);
       byte[] fichero = new byte[tamFichero];
       
       //Guarda fichero
       for(int i=0 ; i<fichero.length;i++) {
			fichero[i] = bytes[tamClave+i];
		}
       
       //System.out.println("textocifrado"+bytes.length+" claveaesleng"+clave.length);
       System.out.println("DESENCRIPTA: textoini-"+" claveaes"+clave.length+" textocript-"+tamFichero+"...completo"+bytes.length);
       
       Cipher rsa = Cipher.getInstance("RSA");
       rsa.init(Cipher.DECRYPT_MODE, privateKey);
       //casca
       byte[] claveAES = rsa.doFinal(clave);
       String claveDesencriptado = new String(claveAES);
       
       Key key = new SecretKeySpec(claveDesencriptado.getBytes(),  0, 16, "AES");
       Cipher aes = Cipher.getInstance("AES");
       
       aes.init(Cipher.DECRYPT_MODE, key);
       byte[] desencriptado = aes.doFinal(fichero);
       
       File salida = new File("TextoDesenciptado.txt");
       FileOutputStream outputStream2 = new FileOutputStream(salida);
       outputStream2.write(desencriptado);
       //aqui ya he encriptado la clave de aes con rsa y la tengo en un fichero
       
       outputStream2.close();
     
       
      //ahora tenemos la clave desencriptada
       
	  }
	  catch(Exception ex){
		  System.out.println(ex.getMessage());
          ex.printStackTrace();
		  
	  }   		

   }
   
   static void encrypt(int cipherMode,Key key /*AES key*/,File inputFile,File outputFile, Key publicKey /*RSA*/){
	   try{
		   
		   Cipher cipher = Cipher.getInstance("AES");
	       cipher.init(cipherMode, key);
	       
	       saveKey(key, "claveAES");

	       FileInputStream inputStream = new FileInputStream(inputFile);
	       byte[] inputBytes = new byte[(int) inputFile.length()];
	       inputStream.read(inputBytes);

	       byte[] outputBytes = cipher.doFinal(inputBytes);
	       // ARCHIVO CIFRADO en outputBytes
	       // Contenido del archivo cifrado 
	       // --- antes de escribirlo en el outputFile, hay que concatenarle al principio la clave cifrada
	       
	       // ----->> para ello primero colocamos el metodo que cifra la clave
		      
	       // Obtener la clave para encriptar/desencriptar
	       
	       File fichero = new File("claveAES");
	       FileInputStream fis = new FileInputStream(fichero);
	       int numBtyes = fis.available();
	       byte[] bytes = new byte[numBtyes];
	       fis.read(bytes);
	       fis.close();	 
	       
	       rsa = Cipher.getInstance("RSA");
	       rsa.init(Cipher.ENCRYPT_MODE, publicKey);
	       // CLAVE AES CIFRADA EN RSA -->> en "encriptado"
	       byte[] encriptado = rsa.doFinal(bytes);
	       System.out.print("ENCRIPTA:textoini"+inputBytes.length+" claveaes"+bytes.length+" textocript"+encriptado.length);
	       
	       // Creamos outputStream que contendra el texto cifrado o descifrado segun el siguiente if
	       FileOutputStream outputStream = new FileOutputStream(outputFile);
		
		// Si modo ENCRIPTAR: tenemos que anyadir la clave en la cabecera
	       // Se concatena la clave RSA con el archivo cifrado, en ese orden, para que aparezca en la cabecera

		   
		   
		   // byte tamCabecera[] = new byte[1];
		   
		   // tamCabecera[0] = (byte) encriptado.length;
		    //tamCabecera es negativo cuando pasas a bytes
		       
			ByteArrayOutputStream byteoutput = new ByteArrayOutputStream( );
			//byteoutput.write(tamCabecera[0]);
			byte[] completo = new byte[encriptado.length + outputBytes.length];
			System.arraycopy(encriptado, 0, completo, 0, encriptado.length);
			System.arraycopy(outputBytes, 0, completo, encriptado.length, outputBytes.length);
			// Colocamos la clave primero
			byteoutput.write( encriptado );
			// Concatenamos el texto cifrado
			byteoutput.write( outputBytes );
			//byte completo[] = byteoutput.toByteArray( );
			System.out.println("...completo="+completo.length);
			// Y lo manda al outpuFile que es el encryptedFile (en el main)
	       outputStream.write(completo);
	       
	       File salida = new File("claveRSA.txt");
	       FileOutputStream outputStream2 = new FileOutputStream(salida);
	       outputStream2.write(encriptado);
	       //aqui ya he encriptado la clave de aes con rsa y la tengo en un fichero
	       
	       outputStream.close();
	       inputStream.close();
		   
	   }
	   catch(Exception ex){
		   System.out.println(ex.getMessage());
           ex.printStackTrace();
	   
	   }
   }
   
	
   public static void main(String[] args) {	
	   try {
		   // GENERA clave publica AES
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(128);
	    Key key = keyGenerator.generateKey();    
	    
 	   // Revisar una UNICA ITERACION
    
 	   // Genera par de claves RSA
        final int keySize = 1024;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize); 
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Se salva y recupera de fichero la clave publica
        saveKey(publicKey, "publickey.dat");
        publicKey = loadPublicKey("publickey.dat");

        // Se salva y recupera de fichero la clave privada
        saveKey(privateKey, "privatekey.dat");
        privateKey = loadPrivateKey("privatekey.dat");
 	 
    
	    String nombre = "fichero.txt";
	    // Obtener primero el nombre del archivo (para cuando se haga el selector de archivo a cifrar)
	    	// TODO
	    // Se carga (con el nombre anterior)
		File inputFile = new File(nombre);
		
		// Se crea archivo - que sera el encriptado	
		File encryptedFile = new File(inputFile.getName()+"-ENCRIPTED.txt");
		
		// Se crea archivo - que sera el desencriptado	
		File decryptedFile = new File(inputFile.getName()+"-DECRIPTED.txt");
	
		
		//ahora tengo que encryptar el archivo que tengo con la clave aes	
		//File archivo = new File("fichero.txt-ENCRIPTED.txt");
			
		// pasamos key AES + publickey RSA
	     Crypto.encrypt(Cipher.ENCRYPT_MODE, key,inputFile,encryptedFile, publicKey);
	     Crypto.decrypt(encryptedFile,privateKey);
	     
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
