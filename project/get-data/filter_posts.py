#!/usr/bin/env python
import csv

data_dir = "../data/"

tr_forum_ids = {'32', '33', '34', '35', '12'}

forum_id_idx = 2
subject_idx = 14
text_idx = 15


lines = []
with open(data_dir + 'phpbb_posts.csv') as f:
    lines = [l for l in csv.reader(f)]

tr_posts = [l for l in lines
            if len(l) > 3
            if l[forum_id_idx] in tr_forum_ids
            if not l[subject_idx].startswith('Re')
            if not l[subject_idx] == ""
            if not l[text_idx] == ""
            if len(l[text_idx]) > 300]

with open(data_dir + 'trip-reports/uwcc.csv', 'w') as f:
    writer = csv.writer(f)
    writer.writerow(['title','text'])
    writer.writerows([t[subject_idx:text_idx + 1] for t in tr_posts])
