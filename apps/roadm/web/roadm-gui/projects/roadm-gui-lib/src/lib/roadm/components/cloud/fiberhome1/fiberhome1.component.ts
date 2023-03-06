import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../../roadm/services/storage.service';
import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';
declare var echarts:any;

@Component({
  selector: 'roadm-app-fiberhome1',
  templateUrl: './fiberhome1.component.html',
  styleUrls: ['./fiberhome1.component.css']
})
export class Fiberhome1Component {

  public pic3 = "https://pic4.zhimg.com/80/v2-207e1cf40966e6f3f18fd6558015de3f_720w.jpg";
  public option1:any;
  public option2:any;
  public option3:any;
  public adress:string="10.112.231.75";
  public mirror_image:any[]=["[datastore1] ubuntu-20.04.3-desktop-amd64.iso","[datastore1] ubuntu-18.04.1-desktop-amd64.iso","[datastore1] ubuntu-18.04.5-desktop-amd64.iso","[datastore1] ubuntu-20.04.1-desktop-amd64.iso"]
  public vm:any={
    name:"",
    cpu:"",
    memory:"",
    disk:"",
    ip:"",
    image:"",
    status:""
  }
  public creat_form:any={
    name:"",
    cpu:"",
    memory:"",
    disk:"",
    image:""
  }
  public vmlist:any[]=[];
  public delete_name_list:any[]=[];
  public delete_vm:any;
  protected topoData: string = '';
  constructor(public storage:StorageService,
              protected fs: FnService,
              protected log: LogService,
              protected wss: WebSocketService,) {
  }
  vmpush(){
      this.vmlist.push(JSON.parse(JSON.stringify(this.vm)));
      this.storage.set('vmlist',this.vmlist);//装入服务
      this.delete_name_list.push(JSON.parse(JSON.stringify(this.vm.name)));
      this.storage.set('namelist',this.delete_name_list);

  }
  del() {
    let del_v=this.delete_vm
    console.log("shancuyuansu",this.delete_vm);
    let index=-1
    for(let i=0;i<this.vmlist.length;i++){
        console.log(this.vmlist[i])
        if(this.vmlist[i].name==del_v){
            index=i
        }
    }
    console.log("index",index);
    if(index>-1){
        this.vmlist.splice(index,1);
        console.log(this.vmlist);
        this.delete_name_list.splice(index,1);
        this.storage.set('vmlist',this.vmlist);
        this.storage.set('namelist',this.delete_name_list);
    }
    window.location.reload();

  }
  clear(){
  this.vmlist=[]
  this.delete_name_list=[]
  this.storage.set('vmlist',this.vmlist);
  this.storage.set('namelist',this.delete_name_list);

  }
  SendMessageToBackward(){
            if(this.wss.isConnected){
                this.wss.sendEvent('helloworldRequest123',{
                'name1':this.creat_form.name,
                'cpu':this.creat_form.cpu,
                'memory':this.creat_form.memory,
                });
                this.log.info('websocket发送helloworld成功');
            }
  }
 ReceiveMessageFromBackward(){
         this.wss.bindHandlers(new Map<string,(data)=>void>([
             ['hiResponse',(data)=>{
                 this.log.info(data);
                 this.topoData = data["receive message"];
                 console.log('jieshou',this.topoData);
                 console.log(typeof this.topoData);
             }]
         ]));

//          this.handlers.push('hiResponse');
//          this.SendMessageToBackward();

   }
  submit() {
    this.vm.name=this.creat_form.name;
    this.vm.cpu=this.creat_form.cpu;
    this.vm.memory=this.creat_form.memory
    this.vm.disk=this.creat_form.disk
    this.vm.ip="待确定"
    this.vm.image=this.creat_form.image
    this.vm.status="待确定"
    this.storage.set('vm',this.vm);
    this.SendMessageToBackward();
    this.vmpush();
    console.log("列表长度",this.vmlist.length)
  }
  ngOnInit() {
    let myChart1=echarts.init(document.getElementById('bar1'));
                  this.option1 = {
                      tooltip: {
                        trigger: 'item'
                      },
                      legend: {
                        top: '5%',
                        left: 'center'
                      },
                      series: [
                        {
                          name: 'Access From',
                          type: 'pie',
                          radius: ['40%', '70%'],
                          avoidLabelOverlap: false,
                          label: {
                            show: false,
                            position: 'center'
                          },
                          emphasis: {
                            label: {
                              show: true,
                              fontSize: '40',
                              fontWeight: 'bold'
                            }
                          },
                          labelLine: {
                            show: false
                          },
                          data: [
                            { value: 700, name: '占用' },
                            { value: 300, name: '空闲' }
                          ]
                        }
                      ]
                  };
                  myChart1.setOption(this.option1);

    let myChart2=echarts.init(document.getElementById('bar2'));
                      this.option2 = {
                          tooltip: {
                            trigger: 'item'
                          },
                          legend: {
                            top: '5%',
                            left: 'center'
                          },
                          series: [
                            {
                              name: 'Access From',
                              type: 'pie',
                              radius: ['40%', '70%'],
                              avoidLabelOverlap: false,
                              label: {
                                show: false,
                                position: 'center'
                              },
                              emphasis: {
                                label: {
                                  show: true,
                                  fontSize: '40',
                                  fontWeight: 'bold'
                                }
                              },
                              labelLine: {
                                show: false
                              },
                              data: [
                                { value: 650, name: '占用' },
                                { value: 350, name: '空闲' }
                              ]
                            }
                          ]
                      };
                      myChart2.setOption(this.option2);

    let myChart3=echarts.init(document.getElementById('bar3'));
                      this.option3 = {
                          tooltip: {
                            trigger: 'item'
                          },
                          legend: {
                            top: '5%',
                            left: 'center'
                          },
                          series: [
                            {
                              name: 'Access From',
                              type: 'pie',
                              radius: ['40%', '70%'],
                              avoidLabelOverlap: false,
                              label: {
                                show: false,
                                position: 'center'
                              },
                              emphasis: {
                                label: {
                                  show: true,
                                  fontSize: '40',
                                  fontWeight: 'bold'
                                }
                              },
                              labelLine: {
                                show: false
                              },
                              data: [
                                { value: 600, name: '占用' },
                                { value: 400, name: '空闲' }
                              ]
                            }
                          ]
                      };
                      myChart3.setOption(this.option3);




    let list1=this.storage.get('vmlist')//导出服务
        if(list1){
          this.vmlist=list1;
        }
        let obj1=this.storage.get('vm')//导出服务
        if(obj1){
          this.vm=obj1;
        }
        let name1=this.storage.get('namelist')
        if(name1){
          this.delete_name_list=name1;
        }

  }
}
