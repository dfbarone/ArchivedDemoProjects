using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;
using System.IO;

namespace airtime_test_dotnet
{
    public class Node
    {
        public Node()
        {
            roomId = string.Empty;
            child = new List<Node>();
            order = 0;
            visited = false;
        }

        public Node(string id)
        {
            roomId = id;
            child = new List<Node>();
            order = 0;
            visited = false;
        }

        public string roomId { get; set; }
        public List<Node> child { get; set; }
        public int order { get; set; }
        public string writing { get; set; }
        public bool visited { get; set; }
    }

    public class Drone
    {
        public enum DroneState
        {
            explore,
            read
        }

        public string id { get; set; }
        public string roomId { get; set; }
    }

    class Test
    {
        public static string URL = "http://challenge2.airtime.com:10001";

        public Node rootNode = new Node();
        public List<Drone> drones = new List<Drone>();
        public Dictionary<string, Node> nodeMap = new Dictionary<string, Node>();

        public async Task<bool> Initialize()
        {
            string content = await jsonRpcComm.Get(URL + "/start");

            if (string.IsNullOrEmpty(content))
                return false;

            JToken jsonObject;
            JObject payload = JObject.Parse(content);

            if (payload.TryGetValue("roomId", out jsonObject))
            {
                rootNode = new Node((string)jsonObject);
                nodeMap.Add(rootNode.roomId, rootNode);
                Console.WriteLine("roomId..." + rootNode.roomId);
                System.Diagnostics.Debug.WriteLine("roomId..." + rootNode.roomId);
            }
            else
            {
                return false;
            }

            if (payload.TryGetValue("drones", out jsonObject))
            {
                drones.Clear();
                List<String> droneNameList = jsonObject.ToObject<List<string>>();
                foreach (string s in droneNameList)
                {
                    Drone d = new Drone();
                    d.id = s;
                    drones.Add(d);
                    Console.WriteLine("drone..." + s);
                    System.Diagnostics.Debug.WriteLine("drone..." + s);
                }

                if (drones.Count < 1)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
            return true;
        }

        public async Task<bool> findConnections(Node parentNode, string drone)
        {
            string command = string.Format("{{ \"<commandId>\":{{\"explore\":\"{0}\"}} }}", parentNode.roomId);
            string content = await jsonRpcComm.Post(URL + "/drone/" + drone + "/commands", command);

            JToken jsonObject;
            JObject payload = JObject.Parse(content);

            if (payload.TryGetValue("<commandId>", out jsonObject))
            {
                JObject obj = jsonObject.ToObject<JObject>();
                if (obj.TryGetValue("connections", out jsonObject))
                {
                    JArray connections = jsonObject.ToObject<JArray>();
                    List<Node> newNodes = new List<Node>();
                    foreach (string s in connections)
                    {
                        Node newNode = new Node(s);
                        if (!nodeMap.ContainsKey(s))
                        {
                            newNodes.Add(newNode);
                            nodeMap.Add(s, newNode);
                            Console.WriteLine("new roomId..." + s);
                            System.Diagnostics.Debug.WriteLine("new roomId..." + s);
                        }
                        else
                        {
                            newNodes.Add(nodeMap[s]);
                            Console.WriteLine("already found roomId..." + s);
                            System.Diagnostics.Debug.WriteLine("already found roomId..." + s);
                        }
                    }

                    // if (parentNode.child.Count == 0)
                    parentNode.child = newNodes;

                    Console.WriteLine("discover..." + parentNode.roomId);
                    System.Diagnostics.Debug.WriteLine("discover..." + parentNode.roomId);
                }
            }
            return true;
        }

        public async Task<bool> findWritings(Node parentNode, string drone)
        {
            string command = string.Format("{{ \"<commandId>\":{{\"read\":\"{0}\"}} }}", parentNode.roomId);
            string content = await jsonRpcComm.Post(URL + "/drone/" + drone + "/commands", command);

            JToken jsonObject;
            JObject payload = JObject.Parse(content);

            if (payload.TryGetValue("<commandId>", out jsonObject))
            {
                string writing = string.Empty;
                int order = 0;
                JObject obj = jsonObject.ToObject<JObject>();
                if (obj.TryGetValue("writing", out jsonObject))
                {
                    writing = (string)jsonObject;
             
                }

                if (obj.TryGetValue("order", out jsonObject)) 
                {
                    order = int.Parse((string)jsonObject);
                    if (order < 0)
                        return false;
                }
       
                parentNode.writing = nodeMap[parentNode.roomId].writing = writing;
                parentNode.order = nodeMap[parentNode.roomId].order = order;
                Console.WriteLine("new read..." + writing + ".." + order);
                System.Diagnostics.Debug.WriteLine("new read..." + writing + ".." + order); 
            }
            return true;
        }

        public async Task<bool> Explore(Node parentNode, string drone)
        {
            bool firstNode = parentNode.roomId == rootNode.roomId;

            if (!parentNode.visited)
            {
                parentNode.visited = true;
                await findConnections(parentNode, firstNode ? drones[0].id : drone);
                await findWritings(parentNode, firstNode ? drones[0].id : drone);   
            }

            // While not empty
            int droneCount = 0;
            foreach (Node childNode in parentNode.child)
            {
                if (!childNode.visited)
                {
                    await Explore(childNode, drones[droneCount].id);
                    //droneCount++;
                }
            }

            return true;
        }

        public async void printMessage()
        {
            Dictionary<int, string> msgMap = new Dictionary<int, string>();
            foreach (Node n in nodeMap.Values)
            {
                if (n.writing != "-1" && !string.IsNullOrEmpty(n.writing))
                    msgMap.Add(n.order, n.writing);
            }

            List<string> msgList = msgMap.OrderBy(e => e.Key).Select(e => e.Value).ToList();

            string message = string.Empty;
            foreach(String s in msgList)
            {
                message += s;
            }

            string command = string.Format("{{ \"message\":\"{0}\" }}", message);
            string content = await jsonRpcComm.Post(URL + "/report", command);

            Console.WriteLine(content);
        }

        public async void Execute()
        {
            // Get marching orders
            await Initialize();

            // Explore the caverns
            await Explore(rootNode, "");

            int size = nodeMap.Count();
            Console.WriteLine("final count " + size);

            // Generate message
            printMessage();

            Console.WriteLine("done");
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            Test t = new Test();
            t.Execute();

            var line = Console.ReadLine();
        }
    }
}
