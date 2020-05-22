import csv
from collections import defaultdict


columns = defaultdict(list)


with open('Breadth_and_Depth_Courses.csv', 'r') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        for(k, v) in row.items():
            columns[k].append(v)

temp = list()
for i in range(len(columns['Subject'])):
    #if(columns['Hum'][i] == '1'):
    #if(columns['SS'][i] == '1'):
    #if(columns['PS'][i] == '1'):
    if(columns['PAS'][i] == '1'):
        temp.append(columns['Subject'][i])
subjects = ";".join(temp)
print(subjects)
