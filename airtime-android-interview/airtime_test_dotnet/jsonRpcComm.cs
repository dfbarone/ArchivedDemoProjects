using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace airtime_test_dotnet
{
    class jsonRpcComm
    {
        public static string email = "jabberwookie@gmail.com";
        public static async Task<string> Get(string url)
        {
            var client = new HttpClient();
            var request = new HttpRequestMessage(HttpMethod.Get, url);
            request.Headers.Add("x-commander-email", email);
            var response = await client.SendAsync(request);

            string content = string.Empty;

            if (response.IsSuccessStatusCode)
                content = await response.Content.ReadAsStringAsync();

            return content;
        }

        public static async Task<string> Post(string url, string jsonData)
        {
            var client = new HttpClient();
            StringContent strContent = new StringContent(jsonData, Encoding.UTF8, "application/json");
            strContent.Headers.Add("x-commander-email", email);
            var response = await client.PostAsync(url, strContent);

            string content = string.Empty;

            if (response.IsSuccessStatusCode)
            {
                content = await response.Content.ReadAsStringAsync();
            }
            else if (response.StatusCode == System.Net.HttpStatusCode.BadRequest)
            {
                content = string.Format("{{ \"error\":\"{0}\" }}", response.StatusCode);
            }  

            return content;
        }
    }
}
