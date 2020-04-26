package de.fs92.defi.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JavaPropertiesTest {
  private static final String UNISWAP_SELL_PROFIT_PERCENTAGE = "uniswapSellProfitPercentage";
  private static JavaProperties javaProperties;

  @BeforeAll
  public static void setUp() {
    javaProperties = new JavaProperties(true);
  }

  @Test
  public void updateValueNoMissingKeys() {
    javaProperties.updateValue(UNISWAP_SELL_PROFIT_PERCENTAGE, "0.5");
    assertFalse(javaProperties.getValue("uniswapBuyProfitPercentage").isEmpty());
    assertFalse(javaProperties.getValue("password").isEmpty());
  }

  @Test
  public void updateValueCorrectValue() {
    javaProperties.updateValue(UNISWAP_SELL_PROFIT_PERCENTAGE, "0.6");
    assertEquals("0.6", javaProperties.getValue(UNISWAP_SELL_PROFIT_PERCENTAGE));
    javaProperties.updateValue(UNISWAP_SELL_PROFIT_PERCENTAGE, "0.5");
    assertEquals("0.5", javaProperties.getValue(UNISWAP_SELL_PROFIT_PERCENTAGE));
  }
}
