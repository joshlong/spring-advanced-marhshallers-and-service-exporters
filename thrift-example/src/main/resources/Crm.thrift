namespace java org.springframework.thrift.crm

/**
 * the type of the entity object
 */
struct Customer {
 1:required string firstName;
 2:required string lastName;
 3:string email;
 4:i32 id;
}

/**
 *  the CRM service interface
 */
service Crm {

 Customer createCustomer( 1:string fn, 2:string ln, 3:string email );

 Customer getCustomerById( 1:i32 customerId);



}