package com.lottery.gamble.proxy.core.util;

import com.lottery.gamble.entity.BackUser;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class PasswordHelper {
	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
	private String algorithmName = "md5";
	private int hashIterations = 2;

	public void encryptPassword(BackUser user) {
		String salt=randomNumberGenerator.nextBytes().toHex();
		user.setCredentialsSalt(salt);
		String newPassword = new SimpleHash(algorithmName, user.getPassword(), ByteSource.Util.bytes(user.getAccountName()+salt), hashIterations).toHex();
		user.setPassword(newPassword);
	}

}
