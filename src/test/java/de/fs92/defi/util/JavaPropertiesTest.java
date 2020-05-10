package de.fs92.defi.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class JavaPropertiesTest {
  private static final String TEST_PROPERTY = "testProperty";
  private static JavaProperties javaProperties;

  @BeforeAll
  public static void setUp() {
    javaProperties = new JavaProperties(true);
  }

  @Test
  public void getValue_getTestProperty_true() {
    String testProperty = javaProperties.getValue(TEST_PROPERTY);
    assertEquals("true", testProperty);
  }

  @Test
  public void setValue_setTestProperty_falseThenTrue() {
    javaProperties.setValue(TEST_PROPERTY, "false");
    String testProperty = javaProperties.getValue(TEST_PROPERTY);
    assertEquals("false", testProperty);
    javaProperties.setValue(TEST_PROPERTY, "true");
    testProperty = javaProperties.getValue(TEST_PROPERTY);
    assertEquals("true", testProperty);
  }

  @Test
  public void getValue_checkAllImportantProperties_AllNotEmpty() {
    assertFalse(javaProperties.getValue("infuraProjectId").isEmpty());
    assertFalse(javaProperties.getValue("password").isEmpty());
    assertFalse(javaProperties.getValue("transactionsRequireConfirmation").isEmpty());
    assertFalse(javaProperties.getValue("playSoundOnTransaction").isEmpty());
    assertFalse(javaProperties.getValue("uniswapBuyProfitPercentage").isEmpty());
    assertFalse(javaProperties.getValue("uniswapSellProfitPercentage").isEmpty());
    assertFalse(javaProperties.getValue("wallet").isEmpty());
    assertFalse(javaProperties.getValue("minimumEthereumReserveUpperLimit").isEmpty());
    assertFalse(javaProperties.getValue("minimumEthereumReserveLowerLimit").isEmpty());
    assertFalse(javaProperties.getValue("minimumEthereumNecessaryForSale").isEmpty());
    assertFalse(javaProperties.getValue("minimumDaiNecessaryForSale").isEmpty());
  }
}
