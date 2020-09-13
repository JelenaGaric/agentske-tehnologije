import json
import string
import sklearn
import pandas
from sklearn import svm, preprocessing
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import LabelBinarizer
import numpy as np


def predict(data):
    min = 20
    path = r'dataset'
    # path = r'C:\Users\Ana\faks\agentske\agentske-tehnologije'
    # path = r'C:\Users\HP\Documents\Tamara faks\Agentske tehnologije\agentske-tehnologije'

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
    dataframeKillsNew = dataframeKillsNew[dataframeKillsNew.Time <= min]
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
    dataframeGoldNew = dataframeGold[['min_20', 'Address', 'Type']].copy()
    dataframeGoldNew = dataframeGoldNew.rename(columns={"min_20": "golddiff"})
    dataframeGoldNew = dataframeGoldNew.loc[(dataframeGoldNew.Type == 'golddiff')]
    dataframeGoldNew = dataframeGoldNew.drop(['Type'], axis=1)
    dataframeMatches = dataframeMatches[['Address', 'rResult', 'bResult']]
    df = dataframeGoldNew.set_index('Address').join(dataframeMatches.set_index('Address'))
    df = pandas.merge(df, mergedKills, on='Address')

    # ------------------------------STRUCTURES-------------------------------------------

    # TODO:Add types?
    dataframeStructures = pandas.read_csv(path + r'\structures.csv')
    dataframeStructures = dataframeStructures[['Team', 'Address', 'Time', 'Lane']].copy()
    dataframeStructures = dataframeStructures[dataframeStructures.Time <= min]
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
    mergedStruct = mergedStruct.rename(columns={"Lane_BOT_LANE_y": "bBOTLANE_kills"})
    mergedStruct = mergedStruct.rename(columns={"Lane_TOP_LANE_x": "rTOPLANE_kills"})
    mergedStruct = mergedStruct.rename(columns={"Lane_MID_LANE_x": "rMIDLANE_kills"})
    mergedStruct = mergedStruct.rename(columns={"Lane_BOT_LANE_x": "rBOTLANE_kills"})
    mergedStruct = mergedStruct.drop('Team', axis=1)
    df = pandas.merge(df, mergedStruct, on='Address')

    # ------------------------------------------------------------------------------------------------

    # --------------------------------MONSTERS---------------------------------------------------------

    dataframeMonsters = pandas.read_csv(path + r'\monsters.csv')

    dataframeMonsters = dataframeMonsters[['Team', 'Address', 'Time']].copy()
    dataframeMonsters = dataframeMonsters[dataframeMonsters.Time <= min]
    # print(dataframeMonsters)
    dataframeMonstersBlue = dataframeMonsters[dataframeMonsters.Team.str.startswith('b')]
    dataframeMonstersBlue.drop('Team', axis=1)
    # print(dataframeMonstersBlue)
    le = preprocessing.LabelEncoder()
    dataframeMonstersBlue['Team'] = le.fit_transform(dataframeMonstersBlue.Team.values)
    dataframeMonstersBlue = dataframeMonstersBlue.Team.astype(int).groupby(dataframeMonstersBlue.Address).count()
    # print(dataframeMonstersBlue)

    dataframeMonstersRed = dataframeMonsters[dataframeMonsters.Team.str.startswith('r')]
    dataframeMonstersRed.drop('Team', axis=1)
    # print(dataframeMonstersBlue)
    le = preprocessing.LabelEncoder()
    dataframeMonstersRed['Team'] = le.fit_transform(dataframeMonstersRed.Team.values)
    dataframeMonstersRed = dataframeMonstersRed.Team.astype(int).groupby(dataframeMonstersRed.Address).count()
    # print(dataframeMonstersRed)

    mergedMonsters = pandas.merge(dataframeMonstersBlue, dataframeMonstersRed, on='Address')
    mergedMonsters = mergedMonsters.rename(columns={"Team_x": "bMonsters"})
    mergedMonsters = mergedMonsters.rename(columns={"Team_y": "rMonsters"})
    mergedMonsters.to_csv(path + r'\joinedMonsters.csv')
    df = pandas.merge(df, mergedMonsters, on='Address')

    # ----------------------------------------------------------------------------------------

    df.to_csv(path + r'\joined.csv')

    df.reset_index(drop=True, inplace=True)
    X = df.drop(['rResult', 'bResult', 'Address'], axis=1)
    print(X.columns)
    y = df['rResult']

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)

    clf = RandomForestClassifier()
    clf.fit(X_train, y_train)
    predicted = clf.predict(X_test)
    accuracy = accuracy_score(y_test, predicted)
    request = data.split("---")
    print(request)
    fields = list(json.loads(data).values())
    print(fields)
    newFields = []
    goldDiff = int(fields[1]) - int(fields[0])
    redDragon = fields[4]
    blueDragon = fields[5]
    rBot = fields[8]
    rMid = fields[6]
    rTop = fields[7]
    for i, f in enumerate(fields):
        if (i != 0 and i != 1 and i!=4 and i!=5 and i!=6 and i!=7 and i!=8):
            newFields.append(int(f))
    newFields.insert(0,goldDiff)
    newFields.append(blueDragon)
    newFields.append(redDragon)
    newFields.insert(3, rBot)
    newFields.insert(4, rMid)
    newFields.insert(5, rTop)
    print(newFields)
    result = clf.predict(np.array(newFields).reshape(1, -1))
    print("{\"certainty\": \"" + str(accuracy) + "\", \"result\": \"" + str(result[0]) + "\"}")

    return "{\"certainty\": \"" + str(accuracy) + "\", \"result\": \"" + str(result[0]) + "\"}"

    # corr = df.corr()
    # sns.heatmap(corr, cmap="YlGnBu", annot=True, annot_kws={"size": 7})
    # plt.show()
