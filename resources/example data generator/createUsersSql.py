import datetime
import random

# example123
USERPWD = "$2a$10$s/cWZcqSZOM4ekPs2xCyEuV0Ykt9VOjBAflU5J884dt9GNWYuXItu"
USERROLE = 'user'
# admin123
ADMINPWD = "$2a$10$ufspUI98vhko6NuQ.AiPBuWqqmuniMH8giMugQa4gFw0gN6zvJGH6"
ADMINROLE = "global_admin"

CURRENTUSERID = 2
CURRENTADDRESSID = 2
CURRENTBUSINESSID = 1
CURRENTBUSINESSADMINID = 2
CURRENTPRODUCTID = 1
CURRENTINVENTORYITEMID = 1
CURRENTSALEITEMID = 1

# Increment after each consecutive run
RUN_COUNTER = 1
N = 1000

BUSINESSTYPES = ["Accommodation and Food Services",
                 "Retail Trade",
                 "Charitable Organisation",
                 "Non-Profit Organisation"]

adjectivesFile = open("resources/adjectives.txt", "r", encoding="utf-8")
cityNamesFile = open("resources/cityNames.txt", "r", encoding="utf-8")
cleanSuburbsFile = open("resources/cleanSuburbs.txt", "r", encoding="utf-8")
countriesFile = open("resources/countries.txt", "r", encoding="utf-8")
firstNamesFile = open("resources/firstNames.txt", "r", encoding="utf-8")
lastNamesFile = open("resources/lastNames.txt", "r", encoding="utf-8")
regionNamesFile = open("resources/regionNames.txt", "r", encoding="utf-8")
streetNamesFile = open("resources/streetNames.txt", "r", encoding="utf-8")
bioFile = open("resources/bio.txt", "r", encoding="utf-8")
introFile = open("resources/intro.txt", "r", encoding="utf-8")
streetTypesFile = open("resources/streetTypes.txt", "r", encoding="utf-8")
businessNamesFile = open("resources/businessNames.txt", "r", encoding="utf-8")
productNamesFile = open("resources/productNames.txt", "r", encoding="utf-8")

streetTypes = streetTypesFile.readlines()
adjectives = adjectivesFile.readlines()
cityNames = cityNamesFile.readlines()
cleanSuburbs = cleanSuburbsFile.readlines()
countries = countriesFile.readlines()
firstNames = firstNamesFile.readlines()
lastNames = lastNamesFile.readlines()
regionNames = regionNamesFile.readlines()
streetNames = streetNamesFile.readlines()
bio = bioFile.readlines()
intro = introFile.readlines()
businessNames = businessNamesFile.readlines()
productNames = productNamesFile.readlines()


class Business:
    """ Business class.
        Called to create an example business for the system using random data.
    """

    # Class initializer calls methods to set attributes of a business object
    def __init__(self):
        self.name = self.get_name()
        self.description = self.get_description()
        self.type = self.get_type()
        self.streetNumber = self.get_street_number()
        self.streetName = self.get_street_name()
        self.suburb = self.get_suburb()
        self.city = self.get_city()
        self.region = self.get_region()
        self.country = self.get_country()
        self.postcode = self.get_postcode()

    # Chooses a random business name from the business names file
    @staticmethod
    def get_name():
        return random.choice(businessNames).strip()

    # Generates a basic description using the generated business name
    def get_description(self):
        return "Welcome to " + self.name + "! We are currently setting up shop so check back soon for progress!"

    # Chooses a random business type from the four possible business types
    @staticmethod
    def get_type():
        return random.choice(BUSINESSTYPES)

    # Generates a random integer between 1 and 200 to be the street number of the business
    @staticmethod
    def get_street_number():
        return str(random.randint(1, 200))

    # Chooses a random street name from the street names and street types files
    @staticmethod
    def get_street_name():
        return random.choice(streetNames).title().strip() + ' ' + random.choice(streetTypes).title().strip()

    # Chooses a random city name from the city names file
    @staticmethod
    def get_city():
        return random.choice(cityNames).strip()

    # Chooses a random suburb name from the suburb names file
    @staticmethod
    def get_suburb():
        return random.choice(cleanSuburbs).title().strip()

    # Chooses a random region name from the region names file
    @staticmethod
    def get_region():
        return random.choice(regionNames).title().strip()

    # Chooses a random country name from the country names file
    @staticmethod
    def get_country():
        return random.choice(countries).strip()

    # Generates a random integer between 1000 and 9999 to be the postcode of the business
    @staticmethod
    def get_postcode():
        return random.randint(1000, 9999)


class User:
    """ User class.
        Called to create an example user for the system using random data.
    """

    # Class initializer calls methods to set attributes of a user object
    def __init__(self, password):
        self.firstName = self.get_first_name()
        self.middleName = self.get_first_name()
        self.lastName = self.get_last_name()
        self.nickname = self.get_nickname()
        self.dateOfBirth = self.get_dob()
        self.bio = self.create_bio()
        self.email = self.create_email()
        self.streetNumber = self.get_street_number()
        self.streetName = self.get_street_name()
        self.suburb = self.get_suburb()
        self.city = self.get_city()
        self.region = self.get_region()
        self.country = self.get_country()
        self.postcode = self.get_postcode()
        self.password = password

    # Chooses a random first name from the first names file
    @staticmethod
    def get_first_name():
        return random.choice(firstNames).strip()

    # Chooses a random last name from the last names file
    @staticmethod
    def get_last_name():
        return random.choice(lastNames).title().strip()

    # Generates a random nickname by using a random adjective and the chosen first name
    def get_nickname(self):
        return random.choice(adjectives).strip().title() + ' ' + self.firstName

    # Generates a random date string to be used as the date of birth for the fake user
    @staticmethod
    def get_dob():
        return str(random.randint(1900, 2006)) + '-' + str(random.randint(1, 12)) + '-' + str(random.randint(1, 28))

    # Creates the bio for a generated user
    def create_bio(self):
        return random.choice(intro).strip() + ", you can call me " + self.firstName + " or "\
            + self.nickname + " for fun. " + random.choice(bio).strip()

    # Generates a random integer between 1 and 200 to be the street number of the user
    @staticmethod
    def get_street_number():
        return str(random.randint(1, 200))

    # Chooses a random street name from the street names and street types files
    @staticmethod
    def get_street_name():
        return random.choice(streetNames).title().strip() + ' ' + random.choice(streetTypes).title().strip()

    # Generates the users email using the chosen name
    def create_email(self):
        return self.firstName.lower() + self.lastName.lower() + "@gmail.com"

    # Chooses a random city name from the city names file
    @staticmethod
    def get_city():
        return random.choice(cityNames).strip()

    # Chooses a random suburb name from the suburb names file
    @staticmethod
    def get_suburb():
        return random.choice(cleanSuburbs).title().strip()

    # Chooses a random region name from the region names file
    @staticmethod
    def get_region():
        return random.choice(regionNames).title().strip()

    # Chooses a random country name from the country names file
    @staticmethod
    def get_country():
        return random.choice(countries).strip()

    # Generates a random integer between 1000 and 9999 to be the postcode of the users address
    @staticmethod
    def get_postcode():
        return random.randint(1000, 9999)


class Product:
    """ Product class.
        Called to create an example product for the system using random data.
    """

    # Class initializer calls methods to set attributes of a product object
    def __init__(self):
        self.name = self.get_name()
        self.code = self.get_code()
        self.description = self.get_description()

    # Chooses a random product name from the product names file
    @staticmethod
    def get_name():
        return random.choice(productNames).strip()

    # Creates a product code using the first and last characters of
    # the product name and a random int between 100 and 999
    def get_code(self):
        random_string_int = str(random.randint(100, 999))
        return self.name[0] + self.name[-1] + random_string_int

    # Generates the description for a product using a randomly chosen adjective and the product name
    def get_description(self):
        return "The " + random.choice(adjectives).strip() + " " + self.name + ". Buy it today."


class InventoryItem:
    """ Inventory Item class.
            Called to create an example inventory item for the system using random data.
        """
    def __init__(self, product):
        self.bestBefore = self.get_best_before()
        self.expires = self.get_expires()
        self.created = self.get_created()
        self.manufactured = self.get_manufactured()
        self.pricePerItem = self.get_price_per_item()
        self.productId = product.code
        self.quantity = self.get_quantity()
        self.sellBy = self.get_sell_by()
        self.totalPrice = self.get_total_price()

    # Generates a random date string to be used as the best before for the fake inventory item
    @staticmethod
    def get_best_before():
        return str(random.randint(2022, 2025)) + '-' + str(random.randint(1, 12)) + '-' + str(random.randint(1, 28))

    @staticmethod
    def get_created():
        return str(random.randint(2020, 2022)) + '-' + str(random.randint(1, 12)) + '-' + str(random.randint(1, 28))

    # Sets inventory items expiry date to same as generated best before value
    def get_expires(self):
        return self.bestBefore

    # Generates a random date string to be used as the manufactured date for the fake inventory item
    @staticmethod
    def get_manufactured():
        return str(random.randint(2018, 2020)) + '-' + str(random.randint(1, 12)) + '-' + str(random.randint(1, 28))

    # Chooses a random int to be the price per item for an inventory item
    @staticmethod
    def get_price_per_item():
        return random.randint(5, 250)

    # Chooses a random int to be the quantity of a fake inventory item
    @staticmethod
    def get_quantity():
        return random.randint(500, 1000)

    # Sets inventory items sell by date to same as generated best before value
    def get_sell_by(self):
        return self.bestBefore

    # Sets the total price of the inventory item to the quantity multiplied by the price per item
    def get_total_price(self):
        return self.pricePerItem * self.quantity


class SaleListing:
    """ Sale Listing class.
            Called to create an example sale listing for the system using random data.
        """

    def __init__(self, inventory_item):
        self.closes = inventory_item.expires
        self.quantity = random.randint(1, 250)
        self.inventory_item_id = inventory_item.productId
        self.description = self.get_description()
        self.price = self.get_price(inventory_item.pricePerItem)
        self.sold = self.get_sold()

    @staticmethod
    # Sets the more info string for a sale listing
    def get_description():
        return "Buy me now, I am a cool product. Buy Me please!"

    @staticmethod
    # Sets the price of a sale listing to the quantity of the sale listing multiplied by the
    # price per item of the inventory item the sale listing relates to
    def get_price(price_per_item):
        return price_per_item

    @staticmethod
    # In php database, 1 represents TRUE and 0 represents FALSE
    def get_sold():
        false = 0
        return false


def create_sql(user_list, user_id, address_id, role):
    """
    Creates Sql scripts for example users and the example addresses for the users
    Loops through a list of generated user objects and adds the data to a sql query for each user object
    Returns a tuple containing the user address query, the user query, the address Id and the user Id
    """
    user_address_query = "INSERT INTO address (CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB) VALUES "
    user_query = "INSERT INTO user (FIRST_NAME, MIDDLE_NAME, LAST_NAME, NICKNAME, BIO, EMAIL, DATE_OF_BIRTH, ADDRESS_ID, PASSWORD, ROLE, CREATED) VALUES "
    for user in user_list:
        user_address_query += "('{}', '{}', '{}', '{}', '{}', '{}', '{}'), ".format(user.city, user.country,
                                                                                    user.postcode, user.region,
                                                                                    user.streetName, user.streetNumber,
                                                                                    user.suburb)
        user_query += "('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}'), ".format(user.firstName,
                                                                                                    user.middleName,
                                                                                                    user.lastName,
                                                                                                    user.nickname,
                                                                                                    user.bio,
                                                                                                    user.email,
                                                                                                    user.dateOfBirth,
                                                                                                    address_id,
                                                                                                    user.password, role,
                                                                                                    str(datetime.datetime.now()).split(
                                                                                                       ' ')[0])
        user_id += 1
        address_id += 1
    return user_address_query[:-2], user_query[:-2], address_id, user_id


def create_business_sql(businesses_list, business_admin_id, business_id, address_id, product_id, products_list,
                        inventory_item_id, inventory_items_list, sale_item_id, sale_items_list):
    """
    Creates Sql scripts for example business and the data related to them such as addresses, admins, and products
    Loops through a list of generated business objects and adds the data to a sql query for each business object
    Five products are created as a part of each business
    Returns a tuple containing the business address query, the business query, the business admins query,
    the business products query, the business inventory query, the business sale items query
    and the address Id, the business Id, the business admin Id, product Id,the inventory item Id, and the sale item id
    """
    business_query = "INSERT INTO business (BUSINESS_TYPE, DESCRIPTION, NAME, PRIMARY_ADMIN_ID, REGISTRATION_DATE, ADDRESS_ID) VALUES "
    business_address_query = "INSERT INTO address (CITY, COUNTRY, POSTCODE, REGION, STREET_NAME, STREET_NUMBER, SUBURB) VALUES "
    business_admins = "INSERT INTO admins (USER_ID, BUSINESS_ID) VALUES "
    business_products = "INSERT INTO product (ROW_ID, CREATED, DESCRIPTION, MANUFACTURER, NAME, PRODUCT_ID, RECOMMENDED_RETAIL_PRICE, BUSINESS_ID) VALUES"
    business_inventory = "INSERT INTO inventory_item (BEST_BEFORE, CREATED, EXPIRES, MANUFACTURED, PRICE_PER_ITEM, PRODUCT_ID, QUANTITY, SELL_BY, TOTAL_PRICE, BUSINESS_ID, ROW_ID) VALUES "
    business_sale_listings = "INSERT INTO sale_item (CLOSES, CREATED, MORE_INFO, PRICE, QUANTITY, INVENTORY_ITEM_ID, SOLD) VALUES "
    liked_sale_listings = "INSERT INTO sale_listing_like (SALE_ITEM_ID, USER_ID) VALUES "
    for business in businesses_list:
        business_address_query += "('{}', '{}', '{}', '{}', '{}', '{}', '{}'), ".format(business.city, business.country,
                                                                                        business.postcode,
                                                                                        business.region,
                                                                                        business.streetName,
                                                                                        business.streetNumber,
                                                                                        business.suburb)
        business_query += "('{}', '{}', '{}', '{}', '{}', '{}'), ".format(business.type,
                                                                                business.description,
                                                                                business.name,
                                                                                business_admin_id,
                                                                                str(datetime.datetime.now()).split(
                                                                                       ' ')[0], address_id)
        business_admins += "('{}', {}), ".format(business_admin_id, business_id)
        for x in range(product_id, product_id+5):
            row_id = x
            product = products_list[x-1]
            inventory_item = inventory_items_list[x-1]
            sale_item = sale_items_list[x-1]
            business_products += "('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}'), ".format(row_id, str(datetime.datetime.now()).split(
                                                                                           ' ')[0], product.description,
                                                                                            business.name, product.name,
                                                                                            product.code,
                                                                                            random.randint(5, 300),
                                                                                            business_id)
            business_inventory += ("('{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}'), ".format(
                inventory_item.bestBefore, inventory_item.created, inventory_item.expires, inventory_item.manufactured,
                inventory_item.pricePerItem, product.code, inventory_item.quantity, inventory_item.sellBy,
                inventory_item.totalPrice, business_id, row_id))

            business_sale_listings += ("('{}', '{}', '{}', '{}', '{}', '{}', {}), ".format(
                sale_item.closes, str(datetime.datetime.now()), sale_item.description, sale_item.price,
                sale_item.quantity, inventory_item_id, sale_item.sold))

            lower = (N * RUN_COUNTER) - N + 1
            upper = (N * RUN_COUNTER)

            for i in range(0, random.randint(0, 3)):
                liked_sale_listings += ("('{}', '{}'), ".format(sale_item_id, random.randint(lower, upper)))

            product_id += 1
            inventory_item_id += 1
            sale_item_id += 1

        business_id += 1
        address_id += 1
        business_admin_id += 1

    return business_address_query[:-2], business_query[:-2], business_admins[:-2], business_products[:-2],\
        business_inventory[:-2], business_sale_listings[:-2], liked_sale_listings[:-2],\
        address_id, business_id, business_admin_id, product_id, inventory_item_id, sale_item_id


# Creates empty list of users and loops through a list of range N to create N user objects
users = []
for i in range(N + 1):
    users.append(User(USERPWD))

# Sets users result to returned value of  createSql
usersResult = create_sql(users, CURRENTUSERID, CURRENTADDRESSID, USERROLE)

# Opens files for data to be written to then write the data, the files are then closed
userAddress = open('scripts/userAddress.sql', 'w', encoding="utf-8")
users = open('scripts/userAccounts.sql', 'w', encoding="utf-8")

userAddress.writelines(usersResult[0])
users.writelines(usersResult[1])
CURRENTADDRESSID = (usersResult[2])
CURRENTUSERID = usersResult[3]

userAddress.close()
users.close()

# Creates empty list of businesses and loops through a list of range N to create N business objects
businesses = []
for i in range(N):
    businesses.append(Business())

# Creates empty list of products and inventory items and loops through a list
# of range N*5 to create N*5 product objects and N*5 inventory items
products = []
inventory = []
sale_items = []
for i in range(N*5):
    products.append(Product())
    inventory.append(InventoryItem(products[i]))
    sale_items.append(SaleListing(inventory[i]))

# Sets businesses result to the returned value of create_business_sql
businessesResult = create_business_sql(businesses, CURRENTBUSINESSADMINID, CURRENTBUSINESSID, CURRENTADDRESSID,
                                       CURRENTPRODUCTID, products, CURRENTINVENTORYITEMID, inventory,
                                       CURRENTSALEITEMID, sale_items)

# Opens files for data to be written to then write the data, the files are then closed
businessesAddress = open('scripts/businessAddress.sql', 'w', encoding="utf-8")
businesses = open('scripts/businessAccounts.sql', 'w', encoding="utf-8")
businessAdmins = open('scripts/businessAdmins.sql', 'w', encoding="utf-8")
businessProducts = open('scripts/businessProducts.sql', 'w', encoding="utf-8")
businessInventory = open('scripts/businessInventory.sql', 'w', encoding="utf-8")
businessSaleListings = open('scripts/businessSaleListings.sql', 'w', encoding="utf-8")
likedSaleListings = open('scripts/likedSaleListings.sql', 'w', encoding="utf-8")

businessesAddress.writelines(businessesResult[0])
businesses.writelines(businessesResult[1])
businessAdmins.writelines(businessesResult[2])
businessProducts.writelines(businessesResult[3])
businessInventory.writelines(businessesResult[4])
businessSaleListings.writelines(businessesResult[5])
likedSaleListings.writelines(businessesResult[6])
CURRENTADDRESSID = businessesResult[7]
CURRENTBUSINESSID = businessesResult[8]
CURRENTBUSINESSADMINID = businessesResult[9]
CURRENTPRODUCTID = businessesResult[10]
CURRENTINVENTORYITEMID = businessesResult[11]
CURRENTSALEITEMID = businessesResult[12]

businessesAddress.close()
businesses.close()
businessAdmins.close()
businessProducts.close()
businessInventory.close()
businessSaleListings.close()
likedSaleListings.close()

# Creates empty list of admins and loops through a list of range 5 to create 5 admin user objects
admins = []
for i in range(5):
    admins.append(User(ADMINPWD))

# Sets businesses result to the returned value of createSql with call for admin role
adminsResult = create_sql(admins, CURRENTUSERID, CURRENTADDRESSID, ADMINROLE)

# Opens files for data to be written to then write the data, the files are then closed
adminAddress = open('scripts/adminAddress.sql', 'w', encoding="utf-8")
admins = open('scripts/adminAccounts.sql', 'w', encoding="utf-8")

adminAddress.writelines(adminsResult[0])
admins.writelines(adminsResult[1])
CURRENTADDRESSID = (usersResult[2])
CURRENTUSERID = usersResult[3]

adminAddress.close()
admins.close()

# Print the current Ids for users, businesses, addresses, and products
print("Current userId = {}".format(CURRENTUSERID))
print("Current businessId = {}".format(CURRENTBUSINESSID))
print("Current addressId = {}".format(CURRENTADDRESSID))
print("Current businessAdminId = {}".format(CURRENTBUSINESSADMINID))
print("Current productId = {}".format(CURRENTPRODUCTID))
print("Current inventoryItemId = {}".format(CURRENTINVENTORYITEMID))
print("Current saleItemId = {}".format(CURRENTSALEITEMID))
