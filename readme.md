<br/>
<div align="center" id="readme-top">

<h3 align="center"> HTCP</h3>
<p align="center">一个简单的HTTP-TCP代理服务器

[Explore The Docs >>][doc-url]

[View Demo][demo-url] | [Report Bug][issues-url] | [Request Feature][issues-url]

</div>

## About the project

&nbsp;&nbsp;&nbsp;
this is a simple project for a tcp-wrapped or direct http proxy. 
it works like an ASAP. the whole HTCP system containes a frontend and 
an optional backend installed on your ASAP backend server.
the frontend will offer HTTP request handling, transfering request to target.
the backend will search for target data,
and return them to remote request send by frontend.

if you think of it as a Minecraft Bungeecord server, you will easily 
unserstand how it works.

```
  TargetServerProgram --[HTTP]-- HTCPBackend --[TCP]-- HTCPFrontend --[HTTP]-- User
  
  TargetServerProgram --[HTTP]-- HTCPFrontend --[HTTP]-- User

```

## Getting Started

#### Client Install

run command:
`java -jar htcp-client.jar`

config.json can be auto created in the runtime path.

#### Client Configurations

```json
{
  "localhost:8125": [ //listening binds. multi listeners can be added.
    {
      "type": "wrapped", //a listener handler types. "wrapped" is assigned to tcp-wrapped server.
      "wrapper": "frp-dry.top:27143", //the wrapper backend address
      "local": "/mcsm-ac-server-daemon", //relative base url. handler will auto fix youre website origin to it.
      "remote": "localhost:24444" //remote wrapper request address
    },
    {
      "type": "direct", //a direct request handler, works like an ASAP.
      "local": "/api",
      "remote": "localhost:8125"
    },
  ],
  "localhost:11452": [
    {
      "type": "direct",
      "local": "/github",
      "remote": "github.com"
    }
  ]
}
```

#### Server Install

1. Download the server jar file
2. start your server using ```java -jar htcp-backend.jar [port]```

### Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue with the tag "enhancement".
Don't forget to give the project a **star**! Thanks again!

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<hr>

<h4 align="center">HTCP</h4>
<h6 align="center">artifact by GrassBlock2022</h6>

<h6 align="center">(The END)</h6>