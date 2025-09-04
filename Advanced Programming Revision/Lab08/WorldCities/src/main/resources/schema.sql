create table if not exists continents (
  id bigint generated always as identity primary key,
  name varchar(100) not null unique
);

create table if not exists countries (
  id bigint generated always as identity primary key,
  name varchar(150) not null unique,
  code varchar(10),
  continent_id bigint not null,
  constraint fk_country_continent foreign key (continent_id) references continents(id)
);

create table if not exists cities (
  id bigint generated always as identity primary key,
  country_id bigint not null,
  name varchar(200) not null,
  capital boolean not null,
  latitude double precision not null,
  longitude double precision not null,
  constraint fk_city_country foreign key (country_id) references countries(id)
);
