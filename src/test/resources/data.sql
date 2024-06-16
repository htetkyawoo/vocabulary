insert into type(type)
values ('abbreviation'),
       ('adjective'),
       ('adverb'),
       ('anatomy'),
       ('approving'),
       ('biology'),
       ('countable'),
       ('chemistry'),
       ('conjunction'),
       ('determiner'),
       ('disapproving'),
       ('for example'),
       ('especially'),
       ('et cetera'),
       ('exclamation'),
       ('feminine'),
       ('figurative'),
       ('formal'),
       ('British'),
       ('geology'),
       ('geometry'),
       ('grammer'),
       ('humorous'),
       ('intransitive'),
       ('idiom'),
       ('informal'),
       ('linguistics'),
       ('literary'),
       ('medicine'),
       ('modal verb'),
       ('noun'),
       ('negative'),
       ('offensive'),
       ('old-fashioned'),
       ('person'),
       ('philosophy'),
       ('plural'),
       ('politics'),
       ('past participle'),
       ('preposition'),
       ('pronoun'),
       ('past tense'),
       ('phrasal verb'),
       ('somebody'),
       ('slang'),
       ('something'),
       ('singular'),
       ('transitive'),
       ('technical'),
       ('uncountable'),
       ('American'),
       ('usually'),
       ('verb');
insert into lang(lang)
values ('en'),
       ('jp'),
       ('fn'),
       ('mm');

insert into vocabulary(spelling)
values ('aback'),
       ('abacus'),
       ('abandon'),
       ('abandoned');


insert into definition(id, def, vocabulary_id, lang_id, type_id)
values ('2005-12-31 23:59:59', 'be taken aback (by sb/sth) be shocked or surprised by sb/sth', 1, 1, 3);
insert into definition(id, def, vocabulary_id, lang_id, type_id)
values ('2005-12-31 23:59:45', 'တုန်လှုပ်ချောက်ခြားစေသည်။ အံ့အားသင့်စေသည်။', 1, 2, 3);
insert into definition(id, def, vocabulary_id, lang_id, type_id)
values ('2005-12-31 23:59:23', 'frame with small balls which slide on rods, used for counting', 2, 1, 31);
insert into definition(id, def, vocabulary_id, lang_id, type_id)
values ('2005-12-31 23:59:00', 'ပေသီးတွက်ခုံ။', 2, 2, 31);

insert into vocabulary_types(types_id, vocabularies_id)
values (3, 1),
       (31, 2);

insert into account(email, name, password, role, gender)
values
       ('member@gmail.com', 'member', '$2a$10$taGq4K1MBx3cEFr2A6xdK.aU8jTwtaojyVirIFiKqwc.s02aNarcG', 1, 0);
