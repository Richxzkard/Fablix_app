drop procedure if exists add_movie;
delimiter ;;

create procedure add_movie(in title varchar(100), in year int,
                           in director varchar(100), in star varchar(100),
                           in genre varchar(32),out movieId varchar(100),
                           out starId varchar(10),out genreId varchar(100))
begin
    declare len int default 0;
    declare str varchar(100) default '';
    declare num varchar(100) default '';

    set starId=null;
    select stars.id into starId from stars where stars.name=star limit 1;
    if starId IS NULL then
        select stars.id into starId from stars order by stars.id desc limit 1;
        set starId=cast(substring(starId,3,11)  as UNSIGNED INTEGER)+1;
        while length(starId)<7 do
            set starId=concat('0',starId);
        end while ;
        set starId=concat('nm',starId);
        insert into stars values(starId,star,null);
    end if ;

    set genreId=null;
    select genres.id into genreId from genres where genres.name=genre limit 1;
    if genreId IS NULL then
        select genres.id into genreId from genres order by genres.id desc limit 1;
        set genreId=cast(genreId as UNSIGNED INTEGER)+1;
        insert into genres values(genreId,genre);
    end if ;

    set movieId=null;

    select movies.id into movieId from movies where movies.title=title and movies.year=year and movies.director=director limit 1;
    if movieId is null then
        select movies.id into movieId from movies order by movies.id desc limit 1;
        set len =length(movieId);
        while len>0 do
            if substring(movieId,len,1) regexp '[0-9]' then
                set num=concat(substring(movieId,len,1),num);
            else
                set str=concat(substring(movieId,len,1),str);
            end if ;
            set len=len-1;
        end while ;
        set len =length(movieId);
        set movieId=cast(num as UNSIGNED INTEGER)+1;
        while length(movieId)<len-length(str) do
            set movieId=concat('0',movieId);
        end while ;
        set movieId=concat(str,movieId);

        insert into movies values(movieId,title,year,director);
        insert into genres_in_movies value(genreId,movieId);
        insert into stars_in_movies values(starId,movieId);
        insert into ratings values(movieId,0,0);
    else
        set movieId = null;
    end if ;
end;

delimiter ;;


# call add_movie(3,1,1,'star1','lol',@re,@re2,@re3);
# select @re,@re2,@re3;

