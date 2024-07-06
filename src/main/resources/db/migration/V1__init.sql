create table game_room
(
    participant_count int                             not null,
    participant_limit int                             not null,
    id                bigint auto_increment
        primary key,
    room_code         binary(16)                      not null,
    creator           varchar(255)                    not null,
    title             varchar(255)                    not null,
    game_status       enum ('NON_PLAYING', 'PLAYING') not null
) engine = InnoDB;

create table member
(
    id                bigint auto_increment
        primary key,
    profile_image_url varchar(500) null,
    refresh_token     varchar(500) null,
    nickname          varchar(255) not null,
    password          varchar(255) not null,
    username          varchar(255) not null
) engine = InnoDB;

create table member_record
(
    avg_time  time(6)        null,
    rate      decimal(38, 2) null,
    id        bigint auto_increment
        primary key,
    lose      bigint         not null,
    member_id bigint         not null,
    win       bigint         not null


) engine = InnoDB;


alter table member
    add constraint UKgc3jmn7c2abyo3wf6syln5t2i unique (username);
alter table member
    add constraint UKhh9kg6jti4n1eoiertn2k6qsc unique (nickname);
alter table member_record
    add constraint UK8kgww1gqnsmg6i5a7jv2lhkbx unique (member_id);
alter table member_record
    add constraint FKoqoavloadbpxumdsicluxfc0o foreign key (member_id) references member (id);