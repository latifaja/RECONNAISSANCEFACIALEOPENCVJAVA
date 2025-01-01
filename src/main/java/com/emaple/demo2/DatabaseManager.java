
package com.emaple.demo2;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:access_managementh.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL;");
                System.out.println("Mode WAL activé.");
            } catch (SQLException e) {
                System.out.println("Erreur lors de l'activation du mode WAL : " + e.getMessage());
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        // Créer les tables user et access_log

        String createUsersTable = "CREATE TABLE IF NOT EXISTS user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "prenom TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "face_embedding BLOB NOT NULL" +
                ");";
        String createAdminTable = "CREATE TABLE IF NOT EXISTS admin (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "password TEXT NOT NULL" +
                ");";

        String createAccessLogTable = "CREATE TABLE IF NOT EXISTS access_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "attempt_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "status TEXT," +
                "FOREIGN KEY(user_id) REFERENCES user(id)" +
                ");";



        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createAccessLogTable);
            stmt.execute(createAdminTable);
            System.out.println("Tables créées avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }

    // Ajouter un utilisateur
    /*public static void addUser(String name, String status, byte[] faceEmbedding) {
        String insertUserSQL = "INSERT INTO user (name, status, face_embedding) VALUES (?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, status);
            pstmt.setBytes(3, faceEmbedding);
            pstmt.executeUpdate();
            System.out.println("User added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }*/




    public static void addUser(String name, String prenom, byte[] faceEmbedding) {
        String insertUserSQL = "INSERT INTO user (name, prenom, face_embedding,status) VALUES (?, ?, ?,?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, prenom);
            pstmt.setBytes(3, faceEmbedding);
            pstmt.setString(4, "accee");// L'empreinte faciale sous forme de tableau de bytes
            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }


    // Récupérer tous les utilisateurs
    public static List<byte[]> getAllFaceEmbeddings() {
        List<byte[]> embeddings = new ArrayList<>();
        String selectEmbeddingsSQL = "SELECT face_embedding FROM user";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(selectEmbeddingsSQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                byte[] embedding = rs.getBytes("face_embedding");
                embeddings.add(embedding);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des empreintes faciales : " + e.getMessage());
        }
        return embeddings;
    }

    // Méthode pour récupérer l'id et les embeddings des utilisateurs
    public static void getUsers() {
        String selectSQL = "SELECT id, name FROM user";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Utilisateur : " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
    }

    // Convertir un tableau de bytes en un tableau de floats
    public static float[] byteArrayToFloatArray(byte[] byteArray) {
        float[] floatArray = new float[byteArray.length / Float.BYTES];
        ByteBuffer.wrap(byteArray).asFloatBuffer().get(floatArray);
        return floatArray;
    }

    public static boolean adimnEx(String nom,String pas){
        boolean ver=false;
        try {Connection con=connect();
            PreparedStatement pst= connect().prepareStatement("select * from admin where name=? and password=?");
            pst.setString(1, nom); // ID de l'utilisateur
            pst.setString(2, pas); // Succès ou échec
            ResultSet rs=pst.executeQuery();
            if (rs.next()){
                ver=true;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return ver;
    }
    public static List<User> getAllUsers(){

        List<User> us=new ArrayList<>();
        Connection conn = connect();
        try{
            PreparedStatement pts=conn.prepareStatement("SELECT id, name, prenom FROM user");
            ResultSet rs=pts.executeQuery();
            while (rs.next()){

                User uss=new User(rs.getInt("id"),rs.getString("name"),"user", rs.getString("prenom"));
                us.add(uss) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return us;
    }
    public static   void deleteUsers(int i){

        Connection conn = connect();
        try{
            PreparedStatement pts=conn.prepareStatement("delete from user  where id=?");
            pts.setInt(1,i);
            pts.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void addAdmin(String name, String password) {
        String insertAdminSQL = "INSERT INTO admin (name, password) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertAdminSQL)) {
            pstmt.setString(1, name);    // Nom de l'administrateur
            pstmt.setString(2, password); // Mot de passe de l'administrateur
                  // Longitude
            pstmt.executeUpdate();
            System.out.println("Admin added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding admin: " + e.getMessage());
        }
    }

}
