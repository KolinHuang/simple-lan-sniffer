/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

import {Injectable} from '@angular/core';
import {Observable, of, throwError} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {catchError, switchMap} from "rxjs/operators";
import {AnalysisStatistic, OriginalPacket, Packet, PacketFilterParams, Result} from "../model/common";
import swal from "sweetalert2";

@Injectable()
export class AnalysisService {
    constructor(private http: HttpClient) {
    }

    analysisByBatchId(batchId: number): Observable<Result> {
        return this.http.get<Result>(`${'/analyze/analysisByBatchId'}?batchId=${batchId}`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    isAnalyzing(): Observable<Result> {
        return this.http.get<Result>(`${'/analyze/isAnalyzing'}`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    distinctBatchIds(): Observable<number[]> {
        return this.http.get<number[]>(`${'/analyze/distinctBatchIds'}`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    filterPackets(params: PacketFilterParams): Observable<Packet[]> {
        return this.http.post<Packet[]>(`${'/analyze/filterPackets'}`, params).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    statisticPackets(params: PacketFilterParams): Observable<AnalysisStatistic> {
        return this.http.post<AnalysisStatistic>(`${'/attack/statisticPackets'}`, params).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    getOriginalPackets(originalIds: string[]): Observable<OriginalPacket[]> {
        return this.http.get<OriginalPacket[]>(`${'/analyze/getOriginalPackets'}?originalIds=${originalIds}`).pipe(
            switchMap(data => of(data)),
            catchError((err) => {
                console.error(err);
                this.swalError(err);
                return throwError(err);
            })
        );
    }

    swalError(err) {
        if (err && err.message) {
            swal({
                title: "Unknown Error",
                text: err.message,
                showConfirmButton: false
            }).catch(swal.noop);
        }
        else {
            swal({
                title: "Unknown Error",
                text: JSON.stringify(err),
                showConfirmButton: false
            }).catch(swal.noop);
        }
    }
}
