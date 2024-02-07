package com.sitepark.translate;

public final class TranslationResultStatistic {

  private final long translationTime;

  private final int chunks;

  private final long sourceBytes;

  private final long targetBytes;

  public static final TranslationResultStatistic EMPTY =
      TranslationResultStatistic.builder()
          .translationTime(0)
          .chunks(0)
          .sourceBytes(0)
          .targetBytes(0)
          .build();

  private TranslationResultStatistic(Builder builder) {
    this.translationTime = builder.translationTime;
    this.chunks = builder.chunks;
    this.sourceBytes = builder.sourceBytes;
    this.targetBytes = builder.targetBytes;
  }

  public long getTranslationTime() {
    return this.translationTime;
  }

  public int getChunks() {
    return this.chunks;
  }

  public long getSourceBytes() {
    return this.sourceBytes;
  }

  public long getTargetBytes() {
    return this.targetBytes;
  }

  public TranslationResultStatistic add(TranslationResultStatistic statistic) {
    return TranslationResultStatistic.builder()
        .translationTime(this.translationTime + statistic.getTranslationTime())
        .chunks(this.chunks + statistic.getChunks())
        .sourceBytes(this.sourceBytes + statistic.getSourceBytes())
        .targetBytes(this.targetBytes + statistic.getTargetBytes())
        .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static final class Builder {

    private long translationTime;

    private int chunks;

    private long sourceBytes;

    private long targetBytes;

    private Builder() {}

    private Builder(TranslationResultStatistic result) {
      this.translationTime = result.translationTime;
      this.chunks = result.chunks;
      this.sourceBytes = result.sourceBytes;
      this.targetBytes = result.targetBytes;
    }

    public Builder translationTime(long translationTime) {
      if (translationTime < 0) {
        throw new IllegalArgumentException("translationTime should be greate equals then 0");
      }
      this.translationTime = translationTime;
      return this;
    }

    public Builder chunks(int chunks) {
      if (chunks < 0) {
        throw new IllegalArgumentException("chunks should be greate equals then 0");
      }
      this.chunks = chunks;
      return this;
    }

    public Builder sourceBytes(long sourceBytes) {
      if (sourceBytes < 0) {
        throw new IllegalArgumentException("sourceBytes should be greate equals then 0");
      }
      this.sourceBytes = sourceBytes;
      return this;
    }

    public Builder targetBytes(long targetBytes) {
      if (targetBytes < 0) {
        throw new IllegalArgumentException("targetBytes should be greate equals then 0");
      }
      this.targetBytes = targetBytes;
      return this;
    }

    public TranslationResultStatistic build() {
      return new TranslationResultStatistic(this);
    }
  }
}
