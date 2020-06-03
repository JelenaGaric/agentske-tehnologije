import string

import pandas
from sklearn import svm, preprocessing
from sklearn.metrics import accuracy_score
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelBinarizer

path = r'D:\agentske tehnologije\datasetLol'
#path = r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije'
#dataframeLoL = pandas.read_csv(path+r'\LeagueofLegends.csv')

#dataframeMatches=pandas.read_csv(path + r'\matchinfo.csv')

#dataframeKills=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\kills.csv')

#dataframeBans=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\bans.csv')

#dataframeStructures=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\structures.csv')

#dataframeMonsters=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\monsters.csv')

#df=dataframeKills.set_index('Address').join(dataframeMatches.set_index('Address'), lsuffix='_a', rsuffix='_b')
#print(df)
#df.to_csv(path + r'\joined.csv')


dataframeGold=pandas.read_csv(path + r'\gold.csv')
dataframeMatches=pandas.read_csv(path + r'\matchinfo.csv')
dataframeKills=pandas.read_csv(path + r'\kills.csv')

dataframeKillsNew=dataframeKills[['Team','Address','Time']]
dataframeKillsNew=dataframeKillsNew[dataframeKillsNew.Time<=15]
dataframeKillsBlue=dataframeKillsNew[dataframeKillsNew.Team=='bKills']
dataframeKillsRed=dataframeKillsNew[dataframeKillsNew.Team=='rKills']
le = preprocessing.LabelEncoder()
dataframeKillsBlue['Team']= le.fit_transform(dataframeKillsBlue.Team.values)
dataframeKillsBlue=dataframeKillsBlue.Team.astype(int).groupby(dataframeKillsBlue.Address).count()
dataframeKillsRed['Team'] = LabelBinarizer().fit_transform(dataframeKillsRed.Team.values)
dataframeKillsRed=dataframeKillsRed.Team.astype(int).groupby(dataframeKillsRed.Address).count()
#dataframeKillsNew=dataframeKillsRed.set_index('Address').join(dataframeKillsBlue.set_index('Address'))

mergedKills = pandas.merge(dataframeKillsRed, dataframeKillsBlue, on='Address')
mergedKills=mergedKills.rename(columns={"Team_x": "rKills"})
mergedKills=mergedKills.rename(columns={"Team_y": "bKills"})
#mergedKills.to_csv(path + r'\joined.csv')

dataframeGoldNew=dataframeGold[['min_15','Address','Type']].copy()
dataframeGoldNew=dataframeGoldNew.rename(columns={"min_15": "golddiff"})

#dataframeTest=dataframeGold.loc[(dataframeGoldNew.Address=='http://matchhistory.na.leagueoflegends.com/en/#match-details/TRKR1/710104?gameHash=f2055a2aab2e9282')]
#print(dataframeTest[['Type','min_15']])

#dataframeGoldNew=dataframeGoldNew.loc[(dataframeGoldNew.Type=='goldred')  |  (dataframeGoldNew.Type=='goldblue') ]

dataframeGoldNew=dataframeGoldNew.loc[(dataframeGoldNew.Type=='golddiff')]
dataframeGoldNew=dataframeGoldNew.drop(['Type'], axis=1)
dataframeMatches=dataframeMatches[['Address','rResult','bResult']]
df=dataframeGoldNew.set_index('Address').join(dataframeMatches.set_index('Address'))
df = pandas.merge(df, mergedKills, on='Address')
df.to_csv(path + r'\joined.csv')

df.reset_index(drop=True, inplace=True)
X=df.drop(['rResult','bResult'],axis=1)
y=df['rResult']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)

clf = svm.SVC()
clf.fit(X_train, y_train)
predicted=clf.predict(X_test)
print(accuracy_score(y_test, predicted))

corr=df.corr()
sns.heatmap(corr)
plt.show()

#print(dataframeGoldNew)