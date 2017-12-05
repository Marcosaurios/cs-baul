package baul;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.RandomAccessFile;

import javax.xml.bind.DatatypeConverter;

public class Pruebas {
 
    public static void main(String[] args) throws Exception {
        
    	try {
	    	
	    	// Generamos PublicKey de AES
	        SecretKey key = getSecretEncryptionKey();
	        //System.out.println("Tamano AES Key (bytes -encoded-): "+ key.getEncoded().length+"; bits: "+key.getEncoded().length*8);
	        
	        // Generamos pares de claves RSA
	        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	        keyPairGenerator.initialize(1024); 
	        KeyPair keyPair = keyPairGenerator.generateKeyPair();
	        PublicKey publicKey = keyPair.getPublic();
	        PrivateKey privateKey = keyPair.getPrivate();
	        
	        String nombre = "fichero.txt";
		    // Obtener primero el nombre del archivo (para cuando se haga el selector de archivo a cifrar)
		    	// TODO
		    // Se carga (con el nombre anterior)
			File inputFile = new File(nombre);
			
			// Se crea archivo - que sera el encriptado	
			File encryptedFile = new File(inputFile.getName()+"-ENCRIPTED.txt");
			
			// Se crea archivo - que sera el desencriptado	
			File decryptedFile = new File(inputFile.getName()+"-DECRIPTED.txt");
			
			File aesclave = new File("claveAES.txt");
			FileOutputStream fout = new FileOutputStream(aesclave);
			fout.write(key.getEncoded());
			fout.close();
			
			// 1 - clave AES cifrada con RSA + Archivo en AES 
			encrypt(inputFile, encryptedFile, publicKey, key);
			// 2 - Desciframos, separando
			decrypt(encryptedFile,decryptedFile, privateKey);
			System.out.println("Success.");
    	}
    	catch(Exception ex) {
    		System.out.println(ex.getMessage());
    		ex.printStackTrace();
    	}
    }
    public static void encrypt(File inputFile, File outputFile, PublicKey publicKey, SecretKey key) throws Exception {
    	    	
    	// 1 - Clave AES cifrada con RSA
		Cipher rsa = Cipher.getInstance("RSA");
		rsa.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] AESkeycifradaRSA = rsa.doFinal(key.getEncoded());
    	
		//System.out.println("Tamano AES key CIFRADA en RSA: "+AESkeycifradaRSA.length);
		
		// Encriptamos archivo con AES
    	byte[] archivoAES=encryptAES(inputFile,key);
    	
    	//System.out.println("Tamano archivo en AES: "+archivoAES.length+" Sin cifrar: "+ inputFile.length());
    
		byte[] completo = new byte[AESkeycifradaRSA.length + archivoAES.length];
		System.arraycopy(AESkeycifradaRSA, 0, completo, 0, AESkeycifradaRSA.length);
		System.arraycopy(archivoAES, 0, completo, AESkeycifradaRSA.length, archivoAES.length);
    	
		//System.out.println("Tamano archivo completo cabeceraRSA+archivoAES: "+completo.length);
		
    	// Salidas a archivos:
    	FileOutputStream fsAES = new FileOutputStream("claveAEScifrada.txt");
    	fsAES.write(AESkeycifradaRSA);
    	fsAES.close();
    	
    	FileOutputStream fsarchivo = new FileOutputStream(outputFile);
    	fsarchivo.write(completo);
    	fsarchivo.close();
    	
    }
    public static void decrypt(File inputFile, File outputFile, PrivateKey privateKey) throws Exception{

    	
    	RandomAccessFile f = new RandomAccessFile(inputFile, "r");
    	byte[] inputBytes = new byte[(int)f.length()];
    	f.readFully(inputBytes);
    	f.close();
    
    	// Obtenemos clave AES cifrada en RSA del input
    	byte[] claveAES = new byte[128];
    	System.arraycopy(inputBytes, 0, claveAES, 0, 128);
		//System.out.println("inputbytes: "+inputBytes.length);
    	
    	// Desciframos la clave
    	Cipher rsa = Cipher.getInstance("RSA");
    	rsa.init(Cipher.DECRYPT_MODE, privateKey);
    	byte[] clavedescifrada = rsa.doFinal(claveAES);
    	
    	int tamArchivo = (int) inputFile.length()-claveAES.length;
    	
    	byte[] archivocifrado = new byte[tamArchivo];
    	// Marcamos inicio del archivo
    	//System.out.println("yeee"+index);
    	//System.out.println("inputfile length "+inputFile.length());
    	//System.out.println("inicio "+claveAES.length);
    	//System.out.println("long archivocifrado "+tamArchivo);
    	System.arraycopy(inputBytes, 128, archivocifrado, 0, tamArchivo);
    	
    	// Creamos una key para AES con los valores descifrados
    	SecretKey key = new SecretKeySpec(clavedescifrada,  0, 16, "AES");
    	// Desciframos el archivo cifrado con la clave AES generada previamente
    	byte[] archivodescifrado = decryptAES(archivocifrado,key);
    	
    	FileOutputStream output = new FileOutputStream(outputFile);
    	output.write(archivodescifrado);
    	output.close();
    	
    	// Salida a archivos
    	FileOutputStream os = new FileOutputStream("claveAES - DECRYPTED.txt");
    	os.write(clavedescifrada);
    	os.close();
    }
    
    public static SecretKey getSecretEncryptionKey() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey secKey = generator.generateKey();
        return secKey;
    }
    
    public static byte[] encryptAES(File input,SecretKey key) throws Exception{
    	
    	RandomAccessFile f = new RandomAccessFile(input, "r");
    	byte[] inputBytes = new byte[(int)f.length()];
    	f.readFully(inputBytes);
    	f.close();
    	
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, key);
        
        // Cifra el archivo
        byte[] byteCipherText = aesCipher.doFinal(inputBytes);
        
        return byteCipherText;
    }
    
    public static byte[] decryptAES(byte[] byteCipherText, SecretKey key) throws Exception {

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytePlainText = aesCipher.doFinal(byteCipherText);
//System.out.println("fffff"+DatatypeConverter.printBase64Binary(byteCipherText));
        //byte[] bytePlainText = aesCipher.doFinal(DatatypeConverter.printBase64Binary(byteCipherText).getBytes());
        //System.out.println( Base64.encodeBase64String(bytePlainText) );
        return bytePlainText;
    }
    
    private static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }
}