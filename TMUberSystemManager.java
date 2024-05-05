//Frank Pasztor
//501229996
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class TMUberSystemManager
{
  private Map<String, User> userMap; 
  private ArrayList<User> userList;
  private ArrayList<Driver> drivers;
  private Queue<TMUberService> queue1;
  private Queue<TMUberService> queue2;
  private Queue<TMUberService> queue3;
  private Queue<TMUberService> queue4;
  private Queue<TMUberService>[] serviceQueues;

  public double totalRevenue; // Total revenues accumulated via rides and deliveries
  
  // Rates per city block
  private static final double DELIVERYRATE = 1.2;
  private static final double RIDERATE = 1.5;
  
  // Portion of a ride/delivery cost paid to the driver
  private static final double PAYRATE = 0.1;

  // These variables are used to generate user account and driver ids
  int userAccountId = 900;
  int driverId = 700;

  @SuppressWarnings("unchecked") //Gets Rid of Warning Message for Unchecked Conversion
  public TMUberSystemManager()
  {
    userMap = new HashMap<>();
    userList = new ArrayList<User>();
    drivers = new ArrayList<Driver>();
    serviceQueues = new Queue[]{queue1,queue2,queue3,queue4};
      for (int i = 0; i < serviceQueues.length; i++) {
          serviceQueues[i] = new LinkedList<TMUberService>();
      }
    
    totalRevenue = 0;
  }
  

  // Generate a new user account id
  private String generateUserAccountId()
  {
    return "" + userAccountId + userMap.size();
  }
  

  // Generate a new driver id
  private String generateDriverId()
  {
    return "" + driverId + drivers.size();
  }


  // Given user account id, find user in list of users
  public User getUser(String accountId)
  {
    for (User user : userMap.values())
    {
      if (user.getAccountId().equals(accountId))
        return user;
    }
    return null;
  }
  

  // Check for duplicate user
  private boolean userExists(User user)
  {
    for (User u : userMap.values())
    {
      if (u.equals(user))
        return true;
    }
    return false;
  }
  

  // Check for duplicate driver
  private boolean driverExists(Driver driver)
  {
    
    for (int i = 0; i < drivers.size(); i++)
    {
      if (drivers.get(i).equals(driver))
        return true;
    }
    return false;
  }
  
 
  // Given a user, check if user ride/delivery request already exists in service requests
  private boolean existingRequest(TMUberService req)
  { 
    for (Queue<TMUberService> queue : serviceQueues)
    {
      if (queue.contains(req))
        return true;
    }
    return false;
  } 
 
  
  // Calculate the cost of a ride or of a delivery based on distance 
  private double getDeliveryCost(int distance)
  {
    return distance * DELIVERYRATE;
  }

  private double getRideCost(int distance)
  {
    return distance * RIDERATE;
  }


  // Go through all drivers and see if one is available
  // Choose the first available driver
  private Driver getAvailableDriver()
  {
    for (int i = 0; i < drivers.size(); i++)
    {
      Driver driver = drivers.get(i);
      if (driver.getStatus() == Driver.Status.AVAILABLE)
        return driver;
    }
    return null;
  }


  // Print Information (printInfo()) about all registered users in the system (map)
  public void listAllUsers()
  {
    System.out.println();
    int index = 1;
    for (User user : userMap.values())
    {
      System.out.printf("%-2s. ", index);
      user.printInfo();
      System.out.println(); 
      index++;
    }
  }


  // Print Information (printInfo()) about all registered users in the system (list for sorting methods)
  public void listAllSortedUsers()
  {
    System.out.println();
    int index = 1;
    for (User user : userList)
    {
      System.out.printf("%-2s. ", index);
      user.printInfo();
      System.out.println(); 
      index++;
    }
  }


  // Print Information (printInfo()) about all registered drivers in the system
  public void listAllDrivers()
  {
    System.out.println();
    
    for (int i = 0; i < drivers.size(); i++)
    {
      int index = i + 1;
      System.out.printf("%-2s. ", index);
      drivers.get(i).printInfo();
      Driver driver = drivers.get(i);
      if (driver.getStatus().equals(Driver.Status.DRIVING)){
        System.out.println("From: " + driver.getService().getFrom() + "   To: " + driver.getService().getTo());
      }
      System.out.println(); 
    }
  }


  // Print Information (printInfo()) about all current service requests
  public void listAllServiceRequests()
  {
    for (int i = 0; i < serviceQueues.length; i++) {
      System.out.println("ZONE " + i);
      System.out.println("======\n");
      
      int index = 1; // Reset index for each zone
      for (TMUberService service : serviceQueues[i]) {
          System.out.print(index + ". ------------------------------------------------------------");
          service.printInfo();
          System.out.println("\n");
          index++;
      }
    }
  }



  // Add a new user to the system
  public void registerNewUser(String name, String address, double wallet)
  {
    // Check to make sure name is valid
    if (name == null || name.equals(""))
    {
      throw new InvalidUserNameException("Invalid User Name");
    }
    // Check to make sure address is valid
    if (!CityMap.validAddress(address))
    {
      throw new InvalidUserAddressException("Invalid User Address");
    }
    // Check to make sure wallet amount is valid
    if (wallet < 0)
    {
      throw new InvalidMoneyInWalletException("Invalid Money in Wallet");
    }
    // Check for duplicate user
    User user = new User(generateUserAccountId(), name, address, wallet);
    if (userExists(user))
    {
      throw new UserAlreadyExistsException("User Already Exists in System");
    }
    userMap.put(user.getAccountId(), new User(String.valueOf(userAccountId*10 + userMap.size()), name, address, wallet));
    userList.add(new User(String.valueOf(userAccountId*10 + userMap.size() -1), name, address, wallet));
  }



  // Add a new driver to the system
  public void registerNewDriver(String name, String carModel, String carLicencePlate, String address)
  {
    // Check to make sure name is valid
    if (name == null || name.equals(""))
    {
      throw new InvalidDriverNameException("Invalid Driver Name");
    }
    // Check to make sure car models is valid
    if (carModel == null || carModel.equals(""))
    {
      throw new InvalidCarModelException("Invalid Car Model");
    }
    // Check to make sure car licence plate is valid
    // i.e. not null or empty string
    if (carLicencePlate == null || carLicencePlate.equals(""))
    {
      throw new InvalidCarLicensePlateException("Invalid Car Licence Plate");
    }
    // Check for duplicate driver. If not a duplicate, add the driver to the drivers list
    Driver driver = new Driver(generateDriverId(), name, carModel, carLicencePlate, address);
    if (driverExists(driver))
    {
      throw new DriverAlreadyExistsException("Driver Already Exists in System");
    }
    drivers.add(driver);
  }



  // Request a ride. User wallet will be reduced when drop off happens
  public void requestRide(String accountId, String from, String to)
  {
    // Check valid user account
    User user = getUser(accountId);
    if (user == null)
    {
      throw new UserAccountNotFoundException("User Account Not Found");
    }
    // Check for a valid from and to addresses
    if (!CityMap.validAddress(from))
    {
      throw new InvalidAddressException("Invalid Address");
    }
    if (!CityMap.validAddress(to))
    {
      throw new InvalidAddressException("Invalid Address");
    }
    // Get the distance for this ride
    int distance = CityMap.getDistance(from, to);         // city blocks
    // Distance == 0 or == 1 is not accepted - walk!
    if (!(distance > 1))
    {
      throw new InsufficientTravelDistanceException("Insufficient Travel Distance");
    }
    // Check if user has enough money in wallet for this trip
    double cost = getRideCost(distance);
    if (user.getWallet() < cost)
    {
      throw new InsufficientFundsException("Insufficient Funds");
    }
    // Get an available driver
    Driver driver = getAvailableDriver();
    if (driver == null) 
    {
      throw new NoDriversAvailableException("No Drivers Available");
    }
    // Create the request
    TMUberRide ride = new TMUberRide(from, to, user, distance, cost);  
    int zone = CityMap.getCityZone(from);  
    // Check if existing ride request for this user - only one ride request per user at a time
    if (existingRequest(ride))
    {
      throw new UserAlreadyHasRideRequestException("User Already Has Ride Request");
    }
    if (zone >= 0 && zone < 4) {
      serviceQueues[zone].add(ride);
    } 
    else{
      throw new InvalidZoneException("Invalid Zone");
    }
  }



  // Request a food delivery. User wallet will be reduced when drop off happens
  public void requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId)
  {
    // Validation checks for user account, addresses, restaurant, and food order ID
    User user = getUser(accountId);
    if (user == null)
    {
      throw new UserAccountNotFoundException("User Account Not Found");
    }
    // Check for valid from and to address
    if (!CityMap.validAddress(from))
    {
      throw new InvalidAddressException("Invalid Address");
    }
    if (!CityMap.validAddress(to))
    {
      throw new InvalidAddressException("Invalid Address");
    }
    if (restaurant == null|| restaurant.equals(""))
    {
      throw new InvalidRestaurantException("Invalid Restaurant Name");
    }
    if (foodOrderId == null|| foodOrderId.equals("")|| !foodOrderId.matches("\\d+"))
    {
      throw new InvalidFoodOrderException("Invalid Food Order Number");
    }
    // Calculates delivery cost and makes sure the user has sufficient funds
    int distance = CityMap.getDistance(from, to);
    // Distance must be at least 1 city block
    if (distance == 0)
    {
      throw new InsufficientTravelDistanceException("Insufficient Travel Distance");
    }
    // Check if user has enough money in wallet for this delivery
    double cost = getDeliveryCost(distance);
    if (user.getWallet() < cost)
    {
      throw new InsufficientFundsException("Insufficient Funds");
    }
    // Find an available driver, if any
    Driver driver = getAvailableDriver();
    if (driver == null) 
    {
      throw new NoDriversAvailableException("No Drivers Available");
    }
    TMUberDelivery delivery = new TMUberDelivery(from, to, user, distance, cost, restaurant, foodOrderId);
    int zone = CityMap.getCityZone(from);    
    // Check if existing delivery request for this user for this restaurant and food order #
    if (existingRequest(delivery))
    {
      throw new UserAlreadyHasDeliveryRequestException("User Already Has Delivery Request at Restaurant with this Food Order");
    }
    if (zone >= 0 && zone < serviceQueues.length) {
      serviceQueues[zone].add(delivery);
    } 
    else {
      throw new InvalidZoneException("Invalid Zone");
    }
  }



  // Cancel an existing service request. 
  // parameter request is the index in the serviceRequests array list
  public void cancelServiceRequest(int zone, int request)
  {
    if (zone < 0 || zone >= serviceQueues.length) {
      throw new InvalidZoneException("Invalid Zone #");
    }
    // Check if valid request #
    Queue<TMUberService> queue = serviceQueues[zone];
    if (request < 1 || request > queue.size()) {
        throw new InvalidRequestNumberException("Invalid Request #");
    }
    int currentIndex = 1; //only way to tell the index of a service request in queue (with an iterator)
    Iterator<TMUberService> iterator = queue.iterator();
    while(iterator.hasNext()){
        iterator.next(); 
        if(currentIndex == request){
            iterator.remove(); //remove element at iterator's current position.
        }
        currentIndex++;
    }
  }


  
  // Drop off a ride or a delivery. This completes a service.
  // parameter request is the index in the serviceRequests array list
  public void dropOff(String driverId) {
      Driver driver = null;
      for (Driver d : drivers) {
          if (d.getId().equals(driverId)) {
              driver = d;
              break;
          }
      } 
      if (driver == null) {
          throw new DriverNotFoundException("Driver Not Found");
      }

      if (driver.getStatus() != Driver.Status.DRIVING) {
          throw new NoDriversAvailableException("Driver Not Driving");
      }

      TMUberService service = driver.getService();
      if (service == null) {
          throw new NoDriversAvailableException("No Requests Available");
      }
      // Update total revenues
      totalRevenue += service.getCost();
      // Pay the driver (10% of service cost)
      double paymentToDriver = service.getCost() * PAYRATE;
      driver.pay(paymentToDriver);
      // Deduct from user's wallet
      User user = service.getUser();
      user.setWallet(user.getWallet() - service.getCost()); 

      // Set driver status to AVAILABLE, clear service reference, update address and zone
      driver.setStatus(Driver.Status.AVAILABLE);
      driver.setAddress(service.getTo());
      driver.setDriverZone(CityMap.getCityZone(service.getTo()));
      driver.setService(null);
  }



  // Sort users by name
  public void sortByUserName()
  {
    Collections.sort(userList, new NameComparator());
    listAllSortedUsers();
  }

  private class NameComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      return a.getName().compareTo(b.getName());
    }
  }



  // Sort users by number amount in wallet
  public void sortByWallet()
  {
    Collections.sort(userList, new UserWalletComparator());
    listAllSortedUsers();
  }

  private class UserWalletComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      if (a.getWallet() > b.getWallet()) return 1;
      if (a.getWallet() < b.getWallet()) return -1; 
      return 0;
    }
  }



  public void pickup(String driverId) {
    Driver driver = null;
    // Find the driver by ID using a traditional for-loop
    for (Driver d : drivers) {
        if (d.getId().equals(driverId)) {
            driver = d;
            break; // Exit the loop once the driver is found
        }
    }
    // Throw an exception if the driver is not found
    if (driver == null) {
        throw new DriverNotFoundException("Driver Not Found");
    }
    // Check if the driver's zone is valid
    int zone = driver.getDriverZone();
    if (zone < 0 || zone >= serviceQueues.length) {
        throw new InvalidZoneException("Invalid Zone");
    }
    // poll a service request from the queue corresponding to the driver's zone
    TMUberService service = serviceQueues[zone].poll(); // gets and removes the head of the queue for this zone
    
    if (service != null) {
        // Assign the service to the driver and update driver's status and address
        System.out.println();
        System.out.println("Driver " + driverId + " Picking Up in Zone " + zone);
        driver.setService(service);
        driver.setStatus(Driver.Status.DRIVING);
        driver.setAddress(service.getFrom()); 
    }else {
        // If no service is available in the queue for this zone
        throw new NoDriversAvailableException("No Service Request in Zone " + zone);
    }
  }


  //used in TMUberUI to load users map and list with array list made from the alotted users.txt file
  public void setUsers(ArrayList<User> userList) {
    userMap.clear();
    for (User user : userList) {
        userMap.put(user.getAccountId(), user);
    }
    for (User user : userMap.values()){
      this.userList.add(user);
    }
  }

  //used in TMUberUI to load drivers list with array list made from the alotted drivers.txt file
  public void setDrivers(ArrayList<Driver> driverList) {
    this.drivers.clear();
    for (Driver driver : driverList) {
        int zone = CityMap.getCityZone(driver.getAddress());
        driver.setDriverZone(zone); 
        this.drivers.add(driver);
    }
  }



public void driveTo(String driverId, String address) {
    // Find the driver by iterating through the list
    Driver driver = null;
    for (Driver d : drivers) {
        if (d.getId().equals(driverId)) {
            driver = d;
            break;
        }
    }
    // Check if the driver was found
    if (driver == null) {
      throw new DriverNotFoundException("Driver Not Found");
    }
    
    // Validate the address
    if (!CityMap.validAddress(address)) {
        throw new InvalidAddressException("Invalid Address");
    }
    
    // Ensure the driver is available
    if (driver.getStatus() != Driver.Status.AVAILABLE) {
        throw new NoDriversAvailableException("Driver Not Available");
    }
    
    // Update the driver's address
    driver.setAddress(address);
    
    // Update the driver's zone based on the new address
    int zone = CityMap.getCityZone(address);
    driver.setDriverZone(zone);
    System.out.println("Driver " + driverId + " Now in Zone " + zone);
  }
}


// Custom exception classes
class InvalidUserNameException extends RuntimeException {
  public InvalidUserNameException(String message) {
      super(message);
  }
}

class InvalidUserAddressException extends RuntimeException {
  public InvalidUserAddressException(String message) {
      super(message);
  }
}

class InvalidMoneyInWalletException extends RuntimeException {
  public InvalidMoneyInWalletException(String message) {
      super(message);
  }
}

class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
      super(message);
  }
}

class InvalidDriverNameException extends RuntimeException {
  public InvalidDriverNameException(String message) {
      super(message);
  }
}

class InvalidCarModelException extends RuntimeException {
  public InvalidCarModelException(String message) {
      super(message);
  }
}

class InvalidCarLicensePlateException extends RuntimeException {
  public InvalidCarLicensePlateException(String message) {
      super(message);
  }
}

class DriverAlreadyExistsException extends RuntimeException {
  public DriverAlreadyExistsException(String message) {
      super(message);
  }
}

class UserAccountNotFoundException extends RuntimeException {
  public UserAccountNotFoundException(String message) {
      super(message);
  }
}

class UserAlreadyHasRideRequestException extends RuntimeException {
  public UserAlreadyHasRideRequestException(String message) {
      super(message);
  }
}

class InsufficientTravelDistanceException extends RuntimeException {
  public InsufficientTravelDistanceException(String message) {
      super(message);
  }
}

class InvalidAddressException extends RuntimeException {
  public InvalidAddressException(String message) {
      super(message);
  }
}

class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message) {
      super(message);
  }
}

class NoDriversAvailableException extends RuntimeException {
  public NoDriversAvailableException(String message) {
      super(message);
  }
}

class UserAlreadyHasDeliveryRequestException extends RuntimeException {
  public UserAlreadyHasDeliveryRequestException(String message) {
      super(message);
  }
}

class InvalidRequestNumberException extends RuntimeException {
  public InvalidRequestNumberException(String message) {
      super(message);
  }
}

class InvalidZoneException extends RuntimeException {
  public InvalidZoneException(String message) {
    super(message);
  }
}

class DriverNotFoundException extends RuntimeException {
  public DriverNotFoundException(String message) {
    super(message);
  }
}

class UserListNotFoundException extends RuntimeException {
  public UserListNotFoundException(String message) {
    super(message);
  }
}

class InvalidRestaurantException extends RuntimeException {
  public InvalidRestaurantException(String message) {
    super(message);
  }
}

class InvalidFoodOrderException extends RuntimeException {
  public InvalidFoodOrderException(String message) {
    super(message);
  }
}