/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Component, OnInit, OnDestroy} from '@angular/core';

import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';

import { ActivatedRoute, Router } from '@angular/router';

/**
 * Model of the response from WebSocket
 */
interface RoadmDeviceTableResponse extends TableResponse {
    roadms: RoadmDevice[];
}

/**
 * Model of the ROADM devices returned from the WebSocket
 */
interface RoadmDevice {
    available: boolean;
    chassisid: string;
    hwVersion: string;
    id: string;
    master: string;
    Vendor: string;
    name: string;
    ports: number;
    protocol: string;
    serial: string;
    swVersion: string;
    type: string;
    _iconid_available: string;
    _iconid_type: string;
}


/**
 * ONOS GUI -- Roadm Device View Component
 */
@Component({
    selector: 'roadm-device',
    templateUrl: './roadm.component.html',
    styleUrls: ['./roadm.component.css', './roadm.theme.css', '../../../fw/widget/table.css', '../../../fw/widget/table.theme.css']
})
export class RoadmDeviceComponent extends TableBaseImpl implements OnInit, OnDestroy {

    // TODO: Update for LION
    flowTip = 'Show flow view for selected device';
    portTip = 'Show port view for selected device';
    groupTip = 'Show group view for selected device';
    meterTip = 'Show meter view for selected device';
    pipeconfTip = 'Show pipeconf view for selected device';

    public picUrl='https://pic2.zhimg.com/80/v2-6a9d7ae6a58b0c147c66222024cd273b_720w.jpg?source=d16d100b';
    public picUrl1='https://pic3.zhimg.com/80/v2-a6c3103f731b16d68a7f4d815969e249_720w.jpg?source=d16d100b';
    public picUrl2='https://pic3.zhimg.com/80/v2-88547b41f9ae52bf84de83f5fa585bc4_720w.jpg?source=d16d100b';
    public picUrl3='https://pic3.zhimg.com/80/v2-d4b450013c2d1fcc9944ccc8457e2fb4_720w.jpg?source=d16d100b';
    public picUrl4='https://pic1.zhimg.com/80/v2-007ed57ff965aa9801a2b98d68ce9d5b_720w.jpg?source=d16d100b';
    public picUrl5='https://pica.zhimg.com/80/v2-cc0723e48cd48e755e7cda0220df21ae_720w.jpg?source=d16d100b';
    public picUrl6='https://pic3.zhimg.com/80/v2-74026d6b34f82eb5ab4b504c2ec93ed1_720w.jpg?source=d16d100b';
    public picUrl7='https://pic1.zhimg.com/80/v2-cab6ca8b44b6dd126804e23fe013e8a7_720w.jpg?source=d16d100b';
    public reveal1:boolean=false;
    public reveal2:boolean=false;
    public reveal3:boolean=false;

    constructor(
        protected fs: FnService,
        protected log: LogService,
        protected as: ActivatedRoute,
        protected router: Router,
        protected wss: WebSocketService,
    ) {
        super(fs, log, wss, 'roadm');
        this.responseCallback = this.deviceResponseCb;

        this.as.queryParams.subscribe(params => {
            this.selId = params['devId'];

        });

        this.payloadParams = {
            devId: this.selId
        };

        this.sortParams = {
            firstCol: 'name',
            firstDir: SortDir.asc,
            secondCol: 'id',
            secondDir: SortDir.desc,
        };
    }

    ngOnInit() {
        this.init();
        this.log.debug('RoadmDeviceComponent initialized');
    }

    ngOnDestroy() {
        this.destroy();
        this.log.debug('RoadmDeviceComponent destroyed');
    }

    deviceResponseCb(data: RoadmDeviceTableResponse) {
        this.log.debug('Device response received for ', data.roadms.length, 'roadm devices');
    }

    navto(path) {
        this.log.debug('navigate');
        if (this.selId) {
            this.router.navigate([path], { queryParams: { devId: this.selId } });
        }
    }

}
