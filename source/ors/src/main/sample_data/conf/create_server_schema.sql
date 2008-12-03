
    drop table if exists id_repository;

    drop table if exists execreports;

    drop table if exists reports;

    drop table if exists ors_users;

    create table ors_users (
        id bigint not null auto_increment,
        lastUpdated datetime,
        updateCount integer not null,
        description varchar(255),
        name varchar(255) not null,
        hashedPassword varchar(255) not null,
        primary key (id),
        unique (name)
    );

    create table reports (
        id bigint not null auto_increment,
        lastUpdated datetime,
        updateCount integer not null,
        destinationID varchar(255) not null,
        fixMessage text not null,
        originator integer,
        reportType integer not null,
        sendingTime datetime not null,
        primary key (id),
        index idx_sendingTime (sendingTime)
    );

    create table execreports (
        id bigint not null auto_increment,
        lastUpdated datetime,
        updateCount integer not null,
        avgPrice numeric(15,5) not null,
        cumQuantity numeric(15,5) not null,
        lastPrice numeric(15,5),
        lastQuantity numeric(15,5),
        orderID varchar(255) not null,
        orderStatus integer not null,
        origOrderID varchar(255),
        rootID varchar(255) not null,
        sendingTime datetime not null,
        side integer not null,
        symbol varchar(255) not null,
        report_id bigint not null,
        primary key (id),
        index idx_report_id (report_id),
        index idx_symbol (symbol),
        index idx_sendingTime (sendingTime),
        index idx_orderID (orderID),
        index idx_rootID (rootID),
        constraint fk_report_id foreign key (report_id) references reports(id)
    );

    create table id_repository (
        id bigint not null auto_increment,
        nextAllowedID bigint not null default 0,
        primary key (id)
    );
