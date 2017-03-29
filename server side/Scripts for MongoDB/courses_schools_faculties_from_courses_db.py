import pymongo

def name_without_spaces_in_begining_or_end(name):
    if (name == None):
        return name
    if (len(name)==0):
        return name
    while(name[0] == " "):
        name = name[1:]
        if (len(name)==0):
            return name
    while(name[len(name)-1] == " "):
        name = name[:len(name)-1]
        if (len(name)==0):
            return name
    return name

def get_exact_course_number(course_number):
    course_number = course_number[:9]
    return course_number


print("started getting info from courses table...")
connection1 = pymongo.MongoClient('mongodb://project:project@ds159737.mlab.com:59737/project')
db1 = connection1['easytau']
col1 = db1["courses"]
col2 = db1["courses"]
col4 = db1["courses"]
tobesaved={}

#cursor1 = col1.distinct("course_name")
#for course_name in cursor1:
#    cursor2 = col2.find({"course_name":course_name}).limit(1)
#    for document in cursor2:
#        school = document["school"]
#        faculty = document["faculty"]

cursor1 = col1.find({})
for document in cursor1:
    course_number = document["course_number"]
    school = document["school"]
    faculty = document["faculty"]
        
    
##    cursor2 = col2.distinct("school", {"course_name":course_name})
##    for school in cursor2:
##        cursor4 = col4.distinct("faculty", {"course_name":course_name})
##        for faculty in cursor4:
    copy_faculty = faculty
    copy_school = school
    copy_course_number = course_number
    clean_faculty = name_without_spaces_in_begining_or_end(copy_faculty)
    clean_school = name_without_spaces_in_begining_or_end(copy_school)
    clean_course_number = name_without_spaces_in_begining_or_end(course_number)
    #clean_course_name = name_without_spaces_in_begining_or_end(copy_course_name)
 #   clean_course_name = copy_course_name #right now dont clean course name because courses table is not clean
    clean_course_number = get_exact_course_number(clean_course_number)
    item = {}
    item["course_number"]=clean_course_number
    if (len(clean_school) == 0):
        clean_school = "קורסים השייכים לפקולטה"
    item["school"]=clean_school
    item["faculty"]=clean_faculty
    if (len(clean_school) != 0 and len(clean_course_number) != 0 and len(clean_faculty) != 0):
        #print("course_name - "+clean_course_name+ " school - "+clean_school)
        tobesaved[clean_course_number] = item
print("finished getting info from courses table")
		
##cursor1 = col1.find({},{"school":1, "faculty":1})
##items_to_be_saved = []
##for document1 in cursor1:
##    school = name_without_spaces_in_begining_or_end(document1["school"])
##    faculty = name_without_spaces_in_begining_or_end(document1["faculty"])
##    item = {}
##    item["school"]=school
##    item["faculty"]=faculty
##    items_to_be_saved.append(item)
    
connection3 = pymongo.MongoClient('mongodb://easytau:easytau@ds159737.mlab.com:59737/easytau')
db3 = connection3['easytau']
col3 = db3["courses_numbers_schools_faculties"]
col3.remove() #first clean the "courses_schools_faculties" table
print("started writing to courses_numbers_schools_faculties table...")
for key in tobesaved:
    #print(tobesaved[key])
    col3.insert(tobesaved[key])
print("finished writing to courses_numbers_schools_faculties table")
##for item_to_be_saved in items_to_be_saved:
##    col3.insert(item_to_be_saved)
