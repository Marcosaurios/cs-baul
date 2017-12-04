
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
	 public static int tamOriginal;
   
   
   static void decrypt(File completo, Key privateKey){
	  try{ 
	   int tamClave=0;
	  	   
	   FileInputStream fis = new FileInputStream(completo);
       int numBtyes = fis.available();
       byte[] bytes = new byte[numBtyes];
       fis.read(bytes);
       fis.close();
       
      


	
       //tamClave = bytes[0];
      
       
       byte[] clave = new byte[16];
            
       //Guarda el tamaño de la clave
       for(int i= 0; i<16;i++) {
			clave[i] = bytes[i];
		}
       System.out.println("clave"+(int)clave[2]);
       
       int tamFichero;
       
       tamFichero = numBtyes - 1 -tamClave;       
       byte[] fichero = new byte[tamFichero];
       
       //Guarda fichero
       for(int i=0 ; i<fichero.length;i++) {
			fichero[i] = bytes[tamClave+1+i];
		}
       
       
       rsa.init(Cipher.DECRYPT_MODE, privateKey);
       byte[] claveAES = rsa.doFinal(clave);
       String claveDesencriptado = new String(claveAES);
       
       Key key = new SecretKeySpec(claveDesencriptado.getBytes(),  0, 16, "AES");
       Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
       
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
   
   static void encrypt(int cipherMode,Key key,File inputFile,File outputFile, Key publicKey){
	   try{
		   
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
		
		// Si modo ENCRIPTAR: tenemos que anyadir la clave en la cabecera
	       // Se concatena la clave RSA con el archivo cifrado, en ese orden, para que aparezca en la cabecera

		   
		   
		   // byte tamCabecera[] = new byte[1];
		   
		   // tamCabecera[0] = (byte) encriptado.length;
		    //tamCabecera es negativo cuando pasas a bytes
		       
			ByteArrayOutputStream byteoutput = new ByteArrayOutputStream( );
			//byteoutput.write(tamCabecera[0]);
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
	       
	       

	       //System.out.println("encript head " + tamanoHeader);
	       //System.out.println("encript original " + tamOriginal);
	       
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
	KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(128);
    Key key = keyGenerator.generateKey();    
    
      
 	   //AQUI PEDIMOS CLAVE Y LA GUARDAMOS Y CONTADOR PARA UNA ITERACION
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
	File archivo = new File("fichero.txt-ENCRIPTED.txt");
			
	     Crypto.encrypt(Cipher.ENCRYPT_MODE, key,inputFile,decryptedFile, publicKey);
	     Crypto.decrypt(archivo,privateKey);
	     
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
