//Frank Pasztor
//501229996
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class TMUberRegistered
{
    // These variables are used to generate user account and driver ids
    private static int firstUserAccountID = 900;
    private static int firstDriverId = 700;

    // Generate a new user account id
    public static String generateUserAccountId(ArrayList<User> current)
    {
        return "" + firstUserAccountID + current.size();
    }

    // Generate a new driver id
    public static String generateDriverId(ArrayList<Driver> current)
    {
        return "" + firstDriverId + current.size();
    }


    /**
     * Loads a list of preregistered users from a specified file.
     * Each user's details are expected to be on consecutive lines with the name first, followed by the address,
     * and the wallet amount.
     *
     * @param filename The path to the file containing the user data.
     * @return A list of User objects initialized from the file data.
     * @throws IOException If there is an error reading from the file.
     */
    public static ArrayList<User> loadPreregisteredUsers(String filename) throws IOException
    {
        ArrayList<User> users = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()){
            String name = scanner.nextLine().trim();
            String address = scanner.nextLine().trim();
            double wallet = Double.parseDouble(scanner.nextLine().trim());
            users.add(new User(generateUserAccountId(users), name, address, wallet));
        }
        scanner.close();
        return users;
    }

    /**
     * Loads a list of preregistered drivers from a specified file.
     * Each driver's details are read from consecutive lines with the name, car model, car license,
     * and address, in that order.
     *
     * @param filename The path to the file containing the driver data.
     * @return A list of Driver objects initialized from the file data.
     * @throws IOException If there is an error reading from the file.
     */
    public static ArrayList<Driver> loadPreregisteredDrivers(String filename) throws IOException
    {
        ArrayList<Driver> drivers = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()){
            String name = scanner.nextLine().trim();
            String carModel = scanner.nextLine().trim();
            String carLicense = scanner.nextLine().trim();
            String address = scanner.nextLine().trim();
            drivers.add(new Driver(generateDriverId(drivers), name, carModel, carLicense, address));
        }
        scanner.close();
        return drivers;
    }
}

