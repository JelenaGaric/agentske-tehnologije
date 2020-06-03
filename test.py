import string

import pandas
from sklearn import svm, preprocessing
from sklearn.metrics import accuracy_score
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelBinarizer

# path = r'D:\agentske tehnologije\datasetLol'
path = r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije'
# dataframeLoL = pandas.read_csv(path+r'\LeagueofLegends.csv')

# dataframeMatches=pandas.read_csv(path + r'\matchinfo.csv')

# dataframeKills=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\kills.csv')

# dataframeBans=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\bans.csv')

# dataframeStructures=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\structures.csv')

# dataframeMonsters=pandas.read_csv(r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije\monsters.csv')

# df=dataframeKills.set_index('Address').join(dataframeMatches.set_index('Address'), lsuffix='_a', rsuffix='_b')
# print(df)
# df.to_csv(path + r'\joined.csv')

# ----------------------KILLS--------------------------------------------------

dataframeMatches = pandas.read_csv(path + r'\matchinfo.csv')
dataframeKills = pandas.read_csv(path + r'\kills.csv')
dataframeKillsNew = dataframeKills[['Team', 'Address', 'Time']]
dataframeKillsNew = dataframeKillsNew[dataframeKillsNew.Time <= 15]
dataframeKillsBlue = dataframeKillsNew[dataframeKillsNew.Team == 'bKills']
dataframeKillsRed = dataframeKillsNew[dataframeKillsNew.Team == 'rKills']
le = preprocessing.LabelEncoder()
dataframeKillsBlue['Team'] = le.fit_transform(dataframeKillsBlue.Team.values)
dataframeKillsBlue = dataframeKillsBlue.Team.astype(int).groupby(dataframeKillsBlue.Address).count()
dataframeKillsRed['Team'] = LabelBinarizer().fit_transform(dataframeKillsRed.Team.values)
dataframeKillsRed = dataframeKillsRed.Team.astype(int).groupby(dataframeKillsRed.Address).count()
# dataframeKillsNew=dataframeKillsRed.set_index('Address').join(dataframeKillsBlue.set_index('Address'))
mergedKills = pandas.merge(dataframeKillsRed, dataframeKillsBlue, on='Address')
mergedKills = mergedKills.rename(columns={"Team_x": "rKills"})
mergedKills = mergedKills.rename(columns={"Team_y": "bKills"})
# mergedKills.to_csv(path + r'\joined.csv')

# dataframeTest=dataframeGold.loc[(dataframeGoldNew.Address=='http://matchhistory.na.leagueoflegends.com/en/#match-details/TRKR1/710104?gameHash=f2055a2aab2e9282')]
# dataframeGoldNew=dataframeGoldNew.loc[(dataframeGoldNew.Type=='goldred')  |  (dataframeGoldNew.Type=='goldblue') ]

# --------------------------------GOLD-----------------------------------------------

dataframeGold = pandas.read_csv(path + r'\gold.csv')
dataframeGoldNew = dataframeGold[['min_15', 'Address', 'Type']].copy()
dataframeGoldNew = dataframeGoldNew.rename(columns={"min_15": "golddiff"})
dataframeGoldNew = dataframeGoldNew.loc[(dataframeGoldNew.Type == 'golddiff')]
dataframeGoldNew = dataframeGoldNew.drop(['Type'], axis=1)
dataframeMatches = dataframeMatches[['Address', 'rResult', 'bResult']]
df = dataframeGoldNew.set_index('Address').join(dataframeMatches.set_index('Address'))
df = pandas.merge(df, mergedKills, on='Address')

# ------------------------------STRUCTURES-------------------------------------------

# TODO:Add types?
dataframeStructures = pandas.read_csv(path + r'\structures.csv')
dataframeStructures = dataframeStructures[['Team', 'Address', 'Time', 'Lane']].copy()
dataframeStructures = dataframeStructures[dataframeStructures.Time <= 15]
dataframeStructures = dataframeStructures.drop('Time', axis=1)
dataframeStructures = pandas.concat([dataframeStructures, pandas.get_dummies(
    dataframeStructures['Lane'], prefix='Lane')], axis=1)
dataframeStructures.drop(['Lane'], axis=1, inplace=True)

dataframeStructuresBlue = dataframeStructures[dataframeStructures.Team.str.startswith('b')]
dataframeStructuresBlue.drop('Team', axis=1)
le = preprocessing.LabelEncoder()
dataframeStructuresBlue = dataframeStructuresBlue.groupby('Address').sum().reset_index()
# dataframeStructuresBlue = count_series.to_frame(name = 'size').reset_index()
# dataframeStructuresBlue.drop(['Lane_TOP_LANE','Lane_MID_LANE','Lane_BOT_LANE'],axis=1)

dataframeStructuresRed = dataframeStructures[dataframeStructures.Team.str.startswith('r')]
dataframeStructuresRed.drop('Team', axis=1)
dataframeStructuresRed['Team'] = LabelBinarizer().fit_transform(dataframeStructuresRed.Team.values)
count_series = dataframeStructuresRed.groupby('Address').sum().reset_index()
# dataframeStructuresRed= count_series.to_frame(name = 'size').reset_index()
# dataframeStructuresRed.drop(['Lane_TOP_LANE','Lane_MID_LANE','Lane_BOT_LANE'],axis=1)

mergedStruct = pandas.merge(dataframeStructuresRed, dataframeStructuresBlue, on=['Address'])
mergedStruct = mergedStruct.rename(columns={"Team_x": "rTowers"})
mergedStruct = mergedStruct.rename(columns={"Team_y": "bTowers"})
mergedStruct = mergedStruct.rename(columns={"Lane_TOP_LANE_y": "bTOPLANE_kills"})
mergedStruct = mergedStruct.rename(columns={"Lane_MID_LANE_y": "bMIDLANE_kills"})
mergedStruct = mergedStruct.rename(columns={"Lane_BOT_LANE_y": "bBOTLANE_kills"})
mergedStruct = mergedStruct.rename(columns={"Lane_TOP_LANE_x": "rTOPLANE_kills"})
mergedStruct = mergedStruct.rename(columns={"Lane_MID_LANE_x": "rMIDLANE_kills"})
mergedStruct = mergedStruct.rename(columns={"Lane_BOT_LANE_x": "rBOTLANE_kills"})
mergedStruct = mergedStruct.drop('Team', axis=1)
df = pandas.merge(df, mergedStruct, on='Address')

# ------------------------------------------------------------------------------------------------

df.to_csv(path + r'\joined.csv')

df.reset_index(drop=True, inplace=True)
X = df.drop(['rResult', 'bResult', 'Address'], axis=1)
y = df['rResult']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)

clf = svm.SVC()
clf.fit(X_train, y_train)
predicted = clf.predict(X_test)
print(accuracy_score(y_test, predicted))

corr = df.corr()
sns.heatmap(corr, cmap="YlGnBu",annot=True, annot_kws={"size": 7})
plt.show()
