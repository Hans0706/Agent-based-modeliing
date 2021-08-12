#!/usr/bin/env python
# coding: utf-8

import pandas as pd
from datetime import datetime
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import KFold



df = pd.read_csv(r"C:\Users\19146\Desktop\HNTrust\wangyi_musi_wordcloud-master\wangyi_musi_wordcloud-master\year_temperature.csv",
)
print(df.head())
df2=pd.read_csv(r"C:\Users\19146\Desktop\HNTrust\wangyi_musi_wordcloud-master\wangyi_musi_wordcloud-master\year.csv")


X = pd.DataFrame(df['time'])
y = pd.DataFrame(df['temperature'])
model = LinearRegression()
predict_X=pd.DataFrame(df2['Year'])
#
# X['tod'] = X.index.hour
# # drop_first = True removes multi-collinearity
# add_var = pd.get_dummies(X['tod'], prefix='tod', drop_first=True)
# # Add all the columns to the model data
# X = X.join(add_var)
# # Drop the original column that was expanded
# X.drop(columns=['tod'], inplace=True)
# print(X.head())


scores2 = []

kfold2 = KFold(n_splits=3, shuffle=True, random_state=42)
for i, (train, test) in enumerate(kfold2.split(X, y)):

 model.fit(X.iloc[train,:], y.iloc[train,:])
 scores2.append(model.score(X.iloc[test,:], y.iloc[test,:]))


print(scores2)
result = model.predict(predict_X)
print(result)
import random
import csv

#python2可以用file替代open
with open("test.csv","w",newline='') as csvfile:
    writer = csv.writer(csvfile)

    #先写入columns_name
    writer.writerow(["b_name"])
    #写入多行用writerows
    for items in result:
        items=items+0.1*items*(1-2*random.uniform(0,2))
        writer.writerow(items)
