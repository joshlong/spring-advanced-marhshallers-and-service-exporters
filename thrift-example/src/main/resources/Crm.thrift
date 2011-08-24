/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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