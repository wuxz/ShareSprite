for i in `cat channels.txt`
	do
	echo dns : $i
	result=`curl http://59.151.117.230/dd/user/$i | head -20 | grep userid | awk -F "\"" '{print $4}'`
	echo $i = $result
	echo $result >> channelsUserIds.txt
done

