/*
 * The gemma project
 *
 * Copyright (c) 2018 University of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubic.gemma.model.expression.experiment;

import ubic.gemma.model.IdentifiableValueObject;
import ubic.gemma.persistence.service.expression.experiment.GeeqServiceImpl;

/**
 * Represents publicly available geeq information
 *
 * @author paul, tesarst
 */
public class GeeqValueObject extends IdentifiableValueObject<Geeq> {

    private double publicQualityScore;
    private double publicSuitabilityScore;

    /*
     * Suitability score factors
     */

    private double sScorePublication;
    private double sScorePlatformAmount;
    private double sScorePlatformsTechMulti;
    private double sScoreAvgPlatformPopularity;
    private double sScoreAvgPlatformSize;
    private double sScoreSampleSize;
    private double sScoreRawData;
    private double sScoreMissingValues;

    /*
     * Quality score factors
     */

    private double qScoreOutliers;
    private double qScoreSampleMeanCorrelation;
    private double qScoreSampleMedianCorrelation;
    private double qScoreSampleCorrelationVariance;
    private double qScorePlatformsTech;
    private double qScoreReplicates;
    private double qScoreBatchInfo;
    private double qScorePublicBatchEffect;
    private double qScorePublicBatchConfound;

    /*
     * Problem/info flags
     */

    private boolean noVectors;
    private byte corrMatIssues;
    private byte replicatesIssues;
    private boolean batchCorrected;

    /**
     * Required when using the class as a spring bean
     */
    public GeeqValueObject() {
    }

    public GeeqValueObject( Object[] row ) {
        super( ( Long ) row[0] );

        this.setPublicQualityScore( ( double ) row[1], ( double ) row[2], ( boolean ) row[3] );
        this.setPublicSuitabilityScore( ( double ) row[4], ( double ) row[5], ( boolean ) row[6] );

        this.sScorePublication = ( double ) row[7];
        this.sScorePlatformAmount = ( double ) row[8];
        this.sScorePlatformsTechMulti = ( double ) row[9];
        this.sScoreAvgPlatformPopularity = ( double ) row[10];
        this.sScoreAvgPlatformSize = ( double ) row[11];
        this.sScoreSampleSize = ( double ) row[12];
        this.sScoreRawData = ( double ) row[13];
        this.sScoreMissingValues = ( double ) row[14];
        this.qScoreOutliers = ( double ) row[15];
        this.qScoreSampleMeanCorrelation = ( double ) row[16];
        this.qScoreSampleMedianCorrelation = ( double ) row[17];
        this.qScoreSampleCorrelationVariance = ( double ) row[18];
        this.qScorePlatformsTech = ( double ) row[19];
        this.qScoreReplicates = ( double ) row[20];
        this.qScoreBatchInfo = ( double ) row[21];
        this.setqScorePublicBatchEffect( ( double ) row[23], ( boolean ) row[24], ( boolean ) row[25],
                ( boolean ) row[26] );
        this.setqScorePublicBatchConfound( ( double ) row[27], ( boolean ) row[28], ( boolean ) row[29] );
        this.noVectors = ( boolean ) row[30];
        this.corrMatIssues = ( byte ) row[31];
        this.replicatesIssues = ( byte ) row[32];
        this.batchCorrected = ( boolean ) row[33];
    }

    public GeeqValueObject( Geeq g ) {
        super( g.getId() );
        this.setPublicQualityScore( g.getDetectedQualityScore(), g.getManualQualityScore(),
                g.getManualQualityOverride() );
        this.setPublicSuitabilityScore( g.getDetectedSuitabilityScore(), g.getManualSuitabilityScore(),
                g.getManualSuitabilityOverride() );
        this.sScorePublication = g.getSScorePublication();
        this.sScorePlatformAmount = g.getSScorePlatformAmount();
        this.sScorePlatformsTechMulti = g.getSScorePlatformsTechMulti();
        this.sScoreAvgPlatformPopularity = g.getSScoreAvgPlatformPopularity();
        this.sScoreAvgPlatformSize = g.getSScoreAvgPlatformSize();
        this.sScoreSampleSize = g.getSScoreSampleSize();
        this.sScoreRawData = g.getSScoreRawData();
        this.sScoreMissingValues = g.getSScoreMissingValues();
        this.qScoreOutliers = g.getQScoreOutliers();
        this.qScoreSampleMeanCorrelation = g.getQScoreSampleMeanCorrelation();
        this.qScoreSampleMedianCorrelation = g.getQScoreSampleMedianCorrelation();
        this.qScoreSampleCorrelationVariance = g.getQScoreSampleCorrelationVariance();
        this.qScorePlatformsTech = g.getQScorePlatformsTech();
        this.qScoreReplicates = g.getQScoreReplicates();
        this.qScoreBatchInfo = g.getQScoreBatchInfo();
        this.setqScorePublicBatchEffect( g.getQScoreBatchEffect(), g.getManualHasStrongBatchEffect(),
                g.getManualHasNoBatchEffect(), g.getManualBatchEffectActive() );
        this.setqScorePublicBatchConfound( g.getQScoreBatchConfound(), g.getManualHasBatchConfound(),
                g.getManualBatchConfoundActive() );
        this.noVectors = g.isNoVectors();
        this.batchCorrected = g.isBatchCorrected();
        this.corrMatIssues = g.getCorrMatIssues();
        this.replicatesIssues = g.getReplicatesIssues();
    }

    public GeeqValueObject( Long id, double detectedQualityScore, double manualQualityScore,
            boolean manualQualityOverride, double detectedSuitabilityScore, double manualSuitabilityScore,
            boolean manualSuitabilityOverride, double sScorePublication, double sScorePlatformAmount,
            double sScorePlatformsTechMulti, double sScoreAvgPlatformPopularity, double sScoreAvgPlatformSize,
            double sScoreSampleSize, double sScoreRawData, double sScoreMissingValues, double qScoreOutliers,
            double qScoreSampleMeanCorrelation, double qScoreSampleMedianCorrelation,
            double qScoreSampleCorrelationVariance, double qScorePlatformsTech, double qScoreReplicates,
            double qScoreBatchInfo, double qScoreBatchEffect, boolean manualHasStrongBatchEffect,
            boolean manualHasNoBatchEffect, boolean manualBatchEffectActive, double qScoreBatchConfound,
            boolean manualHasBatchConfound, boolean manualBatchConfoundActive ) {
        super( id );
        this.setPublicQualityScore( detectedQualityScore, manualQualityScore, manualQualityOverride );
        this.setPublicSuitabilityScore( detectedSuitabilityScore, manualSuitabilityScore, manualSuitabilityOverride );
        this.sScorePublication = sScorePublication;
        this.sScorePlatformAmount = sScorePlatformAmount;
        this.sScorePlatformsTechMulti = sScorePlatformsTechMulti;
        this.sScoreAvgPlatformPopularity = sScoreAvgPlatformPopularity;
        this.sScoreAvgPlatformSize = sScoreAvgPlatformSize;
        this.sScoreSampleSize = sScoreSampleSize;
        this.sScoreRawData = sScoreRawData;
        this.sScoreMissingValues = sScoreMissingValues;
        this.qScoreOutliers = qScoreOutliers;
        this.qScoreSampleMeanCorrelation = qScoreSampleMeanCorrelation;
        this.qScoreSampleMedianCorrelation = qScoreSampleMedianCorrelation;
        this.qScoreSampleCorrelationVariance = qScoreSampleCorrelationVariance;
        this.qScorePlatformsTech = qScorePlatformsTech;
        this.qScoreReplicates = qScoreReplicates;
        this.qScoreBatchInfo = qScoreBatchInfo;
        this.setqScorePublicBatchEffect( qScoreBatchEffect, manualHasStrongBatchEffect, manualHasNoBatchEffect,
                manualBatchEffectActive );
        this.setqScorePublicBatchConfound( qScoreBatchConfound, manualHasBatchConfound, manualBatchConfoundActive );
    }

    private void setPublicQualityScore( double detected, double manual, boolean override ) {
        this.publicQualityScore = override ? manual : detected;
    }

    private void setPublicSuitabilityScore( double detected, double manual, boolean override ) {
        this.publicSuitabilityScore = override ? manual : detected;
    }

    private void setqScorePublicBatchEffect( double detected, boolean manualStrong, boolean manualNone,
            boolean override ) {
        this.qScorePublicBatchEffect = //
                !override ? detected : //
                        manualStrong ? GeeqServiceImpl.BATCH_EFF_STRONG : //
                                manualNone ? GeeqServiceImpl.BATCH_EFF_NONE : GeeqServiceImpl.BATCH_EFF_WEAK;
    }

    private void setqScorePublicBatchConfound( double detected, boolean manualHasConfound, boolean override ) {
        this.qScorePublicBatchConfound = //
                !override ? detected : //
                        manualHasConfound ? GeeqServiceImpl.BATCH_CONF_HAS : GeeqServiceImpl.BATCH_CONF_NOHAS;
    }

    public double getPublicQualityScore() {
        return publicQualityScore;
    }

    public void setPublicQualityScore( double publicQualityScore ) {
        this.publicQualityScore = publicQualityScore;
    }

    public double getPublicSuitabilityScore() {
        return publicSuitabilityScore;
    }

    public void setPublicSuitabilityScore( double publicSuitabilityScore ) {
        this.publicSuitabilityScore = publicSuitabilityScore;
    }

    public double getqScorePublicBatchEffect() {
        return qScorePublicBatchEffect;
    }

    public void setqScorePublicBatchEffect( double qScorePublicBatchEffect ) {
        this.qScorePublicBatchEffect = qScorePublicBatchEffect;
    }

    public double getqScorePublicBatchConfound() {
        return qScorePublicBatchConfound;
    }

    public void setqScorePublicBatchConfound( double qScorePublicBatchConfound ) {
        this.qScorePublicBatchConfound = qScorePublicBatchConfound;
    }

    public double getsScorePublication() {
        return sScorePublication;
    }

    public void setsScorePublication( double sScorePublication ) {
        this.sScorePublication = sScorePublication;
    }

    public double getsScorePlatformAmount() {
        return sScorePlatformAmount;
    }

    public void setsScorePlatformAmount( double sScorePlatformAmount ) {
        this.sScorePlatformAmount = sScorePlatformAmount;
    }

    public double getsScorePlatformsTechMulti() {
        return sScorePlatformsTechMulti;
    }

    public void setsScorePlatformsTechMulti( double sScorePlatformsTechMulti ) {
        this.sScorePlatformsTechMulti = sScorePlatformsTechMulti;
    }

    public double getsScoreAvgPlatformPopularity() {
        return sScoreAvgPlatformPopularity;
    }

    public void setsScoreAvgPlatformPopularity( double sScoreAvgPlatformPopularity ) {
        this.sScoreAvgPlatformPopularity = sScoreAvgPlatformPopularity;
    }

    public double getsScoreAvgPlatformSize() {
        return sScoreAvgPlatformSize;
    }

    public void setsScoreAvgPlatformSize( double sScoreAvgPlatformSize ) {
        this.sScoreAvgPlatformSize = sScoreAvgPlatformSize;
    }

    public double getsScoreSampleSize() {
        return sScoreSampleSize;
    }

    public void setsScoreSampleSize( double sScoreSampleSize ) {
        this.sScoreSampleSize = sScoreSampleSize;
    }

    public double getsScoreRawData() {
        return sScoreRawData;
    }

    public void setsScoreRawData( double sScoreRawData ) {
        this.sScoreRawData = sScoreRawData;
    }

    public double getsScoreMissingValues() {
        return sScoreMissingValues;
    }

    public void setsScoreMissingValues( double sScoreMissingValues ) {
        this.sScoreMissingValues = sScoreMissingValues;
    }

    public double getqScoreOutliers() {
        return qScoreOutliers;
    }

    public void setqScoreOutliers( double qScoreOutliers ) {
        this.qScoreOutliers = qScoreOutliers;
    }

    public double getqScoreSampleMeanCorrelation() {
        return qScoreSampleMeanCorrelation;
    }

    public void setqScoreSampleMeanCorrelation( double qScoreSampleMeanCorrelation ) {
        this.qScoreSampleMeanCorrelation = qScoreSampleMeanCorrelation;
    }

    public double getqScorePlatformsTech() {
        return qScorePlatformsTech;
    }

    public void setqScorePlatformsTech( double qScorePlatformsTech ) {
        this.qScorePlatformsTech = qScorePlatformsTech;
    }

    public double getqScoreReplicates() {
        return qScoreReplicates;
    }

    public void setqScoreReplicates( double qScoreReplicates ) {
        this.qScoreReplicates = qScoreReplicates;
    }

    public double getqScoreBatchInfo() {
        return qScoreBatchInfo;
    }

    public void setqScoreBatchInfo( double qScoreBatchInfo ) {
        this.qScoreBatchInfo = qScoreBatchInfo;
    }

    public double getqScoreSampleMedianCorrelation() {
        return qScoreSampleMedianCorrelation;
    }

    public void setqScoreSampleMedianCorrelation( double qScoreSampleMedianCorrelation ) {
        this.qScoreSampleMedianCorrelation = qScoreSampleMedianCorrelation;
    }

    public double getqScoreSampleCorrelationVariance() {
        return qScoreSampleCorrelationVariance;
    }

    public void setqScoreSampleCorrelationVariance( double qScoreSampleCorrelationVariance ) {
        this.qScoreSampleCorrelationVariance = qScoreSampleCorrelationVariance;
    }

    public boolean isNoVectors() {
        return noVectors;
    }

    public void setNoVectors( boolean noVectors ) {
        this.noVectors = noVectors;
    }

    public byte getCorrMatIssues() {
        return corrMatIssues;
    }

    public void setCorrMatIssues( byte corrMatIssues ) {
        this.corrMatIssues = corrMatIssues;
    }

    public byte getReplicatesIssues() {
        return replicatesIssues;
    }

    public void setReplicatesIssues( byte replicatesIssues ) {
        this.replicatesIssues = replicatesIssues;
    }

    public boolean isBatchCorrected() {
        return batchCorrected;
    }

    public void setBatchCorrected( boolean batchCorrected ) {
        this.batchCorrected = batchCorrected;
    }
}
