package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import javax.sql.DataSource;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import io.micrometer.core.annotation.Timed;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ArchiveRepository extends NamedParameterJdbcTemplate {

    private static final String QUERY_ARCHIVE_PRICES = """
            insert into pricing_entity.archived_price select id, proposition_id, option_id, variant_id, price, updated_at from pricing_entity.price pr where pr.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)
                 on conflict do nothing""";
    private static final String QUERY_ARCHIVE_PLACEHOLDERS = """
            insert into pricing_entity.archived_proposition_placeholder
                select id, name, value, proposition_id from pricing_entity.proposition_placeholder pp where pp.proposition_id in
                    (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)
                 on conflict do nothing""";

    private static final String QUERY_ARCHIVE_VARIANTS = """
            insert into pricing_entity.archived_variant select id, proposition_id, key, name, type, updated_at from pricing_entity.variant v where v.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)
                 on conflict do nothing""";
    private static final String QUERY_ARCHIVE_OPTIONS = """
            insert into pricing_entity.archived_option select id, proposition_id, key, name, type, updated_at from pricing_entity.option o where o.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)
                 on conflict do nothing""";
    private static final String QUERY_ARCHIVE_PROPOSITIONS = """
            insert into pricing_entity.archived_proposition select id, key, contest_id, name, type, updated_at from pricing_entity.proposition p where p.contest_id =
            :contest_id on conflict do nothing""";
    private static final String QUERY_ARCHIVE_CONTEST = """
            insert INTO pricing_entity.archived_contest (id, key, name, status, type, updated_at, start_date_time)
             select id, key, name, status, type, updated_at, start_date_time from pricing_entity.contest c where c.id = :contest_id on conflict do nothing""";

    private static final String QUERY_ARCHIVE_OUTCOMES = """
            insert into pricing_entity.archived_outcome select id, proposition_id, option_id, variant_id, refund_numerator, refund_denominator, win_numerator, win_denominator
             from pricing_entity.outcome o where o.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)
                 on conflict do nothing""";
    private static final String QUERY_DELETE_CONTEST = "delete from pricing_entity.contest c where c.id = :contest_id";
    private static final String QUERY_DELETE_ARCHIVED_CONTEST = "delete from pricing_entity.archived_contest c where c.id = :contest_id";
    private static final String QUERY_DELETE_PRICES = """
            delete from pricing_entity.price pr where pr.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_ARCHIVED_PRICES = """
            delete from pricing_entity.archived_price pr where pr.proposition_id in
                (select p.id from pricing_entity.archived_proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_PLACEHOLDERS = """
            delete from pricing_entity.proposition_placeholder pp where pp.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_ARCHIVED_PLACEHOLDERS = """
            delete from pricing_entity.archived_proposition_placeholder pp where pp.proposition_id in
                (select p.id from pricing_entity.archived_proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_VARIANTS = """
            delete from pricing_entity.variant v where v.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_ARCHIVED_VARIANTS = """
            delete from pricing_entity.archived_variant v where v.proposition_id in
                (select p.id from pricing_entity.archived_proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_OPTIONS = """
            delete from pricing_entity.option o where o.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_ARCHIVED_OPTIONS = """
            delete from pricing_entity.archived_option o where o.proposition_id in
                (select p.id from pricing_entity.archived_proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_PROPOSITIONS = """
            delete from pricing_entity.proposition p where p.contest_id =:contest_id""";
    private static final String QUERY_DELETE_ARCHIVED_PROPOSITIONS = """
            delete from pricing_entity.archived_proposition p where p.contest_id =:contest_id""";
    private static final String QUERY_DELETE_OUTCOMES = """
            delete from pricing_entity.outcome o where o.proposition_id in
                (select p.id from pricing_entity.proposition p where p.contest_id = :contest_id)""";
    private static final String QUERY_DELETE_ARCHIVED_OUTCOMES = """
            delete from pricing_entity.archived_outcome o where o.proposition_id in
                (select p.id from pricing_entity.archived_proposition p where p.contest_id = :contest_id)""";

    public ArchiveRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archivePrices"}, histogram = true)
    public void archivePrices(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_PRICES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archivePlaceholders"}, histogram = true)
    public void archivePlaceholders(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_PLACEHOLDERS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archiveVariants"}, histogram = true)
    public void archiveVariants(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_VARIANTS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archiveOptions"}, histogram = true)
    public void archiveOptions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_OPTIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archivePropositions"}, histogram = true)
    public void archivePropositions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_PROPOSITIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archiveOutcomes"}, histogram = true)
    public void archiveOutcomes(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_OUTCOMES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.archiveContest"}, histogram = true)
    public void archiveContest(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_ARCHIVE_CONTEST, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deletePrices"}, histogram = true)
    public void deletePrices(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_PRICES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedPrices"}, histogram = true)
    public void deleteArchivedPrices(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_PRICES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deletePlaceholders"}, histogram = true)
    public void deletePlaceholders(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_PLACEHOLDERS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedPlaceholders"}, histogram = true)
    public void deleteArchivedPlaceholders(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_PLACEHOLDERS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteVariants"}, histogram = true)
    public void deleteVariants(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_VARIANTS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedVariants"}, histogram = true)
    public void deleteArchivedVariants(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_VARIANTS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteOptions"}, histogram = true)
    public void deleteOptions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_OPTIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedOptions"}, histogram = true)
    public void deleteArchivedOptions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_OPTIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deletePropositions"}, histogram = true)
    public void deletePropositions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_PROPOSITIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedPropositions"}, histogram = true)
    public void deleteArchivedPropositions(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_PROPOSITIONS, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteOutcomes"}, histogram = true)
    public void deleteOutcomes(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_OUTCOMES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedOutcomes"}, histogram = true)
    public void deleteArchivedOutcomes(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_OUTCOMES, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteContest"}, histogram = true)
    public void deleteContest(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_CONTEST, parameters);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveRepository.deleteArchivedContest"}, histogram = true)
    public void deleteArchivedContest(Long contestId) {
        SqlParameterSource parameters = new MapSqlParameterSource("contest_id", contestId);
        update(QUERY_DELETE_ARCHIVED_CONTEST, parameters);
    }

}
