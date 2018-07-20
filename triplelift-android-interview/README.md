# AdAggregator

<p align="left">
  <img src="screenshots/screenshot_date.png" width="350"/>
</p>

Our server has a URL endpoint that will return the performance (number of clicks and impressions) of a given advertiser over the past week (see JSON example below).

Create a Simple iOS or Android app that will do the following:

1. Create a UI that allows me enter to enter multiple advertiser ids<br />
Does not have to be very fancy but more elegant than a comma separated list
2. Have a submit button that on click will:<br />
Make all the needed API calls<br />
Render a table showing the impression and clicks aggregated by date for all the advertiser id�s submitted.
3. Bonus points if:<br />
You make the table sortable<br />
You update the table with data as the api calls come back<br />
Use concurrency and parallelization<br />
4. Style the app to your taste.<br />
 
Note: Take performance into consideration (eg a large number of advertiser id's will be submitted)

Sample JSON returned: <br />
[<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-20", "num_clicks" : 24, "num_impressions" : 1000},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-21", "num_clicks" : 20, "num_impressions" : 1010},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-22", "num_clicks" : 10, "num_impressions" : 1210},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-23", "num_clicks" : 22, "num_impressions" : 1110},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-24", "num_clicks" : 25, "num_impressions" : 1710},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-25", "num_clicks" : 31, "num_impressions" : 1020},<br />
 {"advertiser_id" : 123, "ymd" : "2014-09-26", "num_clicks" : 50, "num_impressions" : 2000}<br />
]<br />
