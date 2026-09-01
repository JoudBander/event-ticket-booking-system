-- Event Ticket Booking System — Database Schema (Oracle)
-- Reconstructed verbatim from the CS313 project report

create table USERS (
    USER_ID     char(10),
    USERNAME    varchar(50)  not null,
    PASSWORD    varchar(50)  not null,
    FULL_NAME   varchar(100) not null,
    EMAIL       varchar(100),
    PHONE       varchar(15),
    USER_TYPE   varchar(20)  not null,
    primary key (USER_ID)
);

create table EVENTS (
    EVENT_ID        char(10),
    EVENT_NAME      varchar(100) not null,
    EVENT_TYPE      varchar(20)  not null,
    EVENT_DATE      date         not null,
    LOCATION        varchar(100) not null,
    TICKET_PRICE    number(10,2) not null,
    TOTAL_SEATS     integer      not null,
    AVAILABLE_SEATS integer      not null,
    MANAGER_ID      char(10),
    primary key (EVENT_ID),
    foreign key (MANAGER_ID) references USERS(USER_ID)
);

create table BOOKINGS (
    BOOKING_ID   char(10),
    CUSTOMER_ID  char(10)     not null,
    EVENT_ID     char(10)     not null,
    BOOKING_DATE date,
    NUM_TICKETS  integer      not null,
    TOTAL_AMOUNT number(10,2) not null,
    STATUS       varchar(20),
    primary key (BOOKING_ID),
    foreign key (CUSTOMER_ID) references USERS(USER_ID),
    foreign key (EVENT_ID) references EVENTS(EVENT_ID)
);
